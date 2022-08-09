import tarfile
import argparse
import os
import requests
import shutil


def main():
    # yay arguments.
    token, organization, repository, branch, server_name, webhook = None, None, None, None, None, None

    parser = argparse.ArgumentParser()

    parser.add_argument('-token')
    parser.add_argument('-organization')
    parser.add_argument('-repository')
    parser.add_argument('-branch')
    parser.add_argument('-servername')
    parser.add_argument('-webhook')

    args = parser.parse_args()

    if args.token:
        token = args.token
    if args.organization:
        organization = args.organization
    if args.repository:
        repository = args.repository
    if args.branch:
        branch = args.branch
    if args.servername:
        server_name = args.servername
    if args.webhook:
        webhook = args.webhook

    github = Github(token, organization, repository, branch)

    if os.path.exists('./plugins'):
        delete_dir('./plugins')

    parse_tar_file(github.get_repo_tar())

    if webhook != "null":
        hook_message = None
        # yes, i don't care enough to handle this properly java-side.
        if server_name != "null":
            hook_message = f"Deployed {server_name} on branch **{branch}**."
        else:
            hook_message = f"Deployed on **{branch}**."

        # Temporary webhook until I care enough to make it nice.
        requests.post(webhook, json={
            "content": hook_message
        })

    # die.
    os._exit(0)


def parse_tar_file(file_name: str):
    try:
        tar_file = tarfile.open(file_name)
        tar_file.extractall("./mhdeploytmp")
        tar_file.close()
    except (tarfile.ReadError):
        print("Failed to find ./mhdeploytmp.")
        return

    def get_dirs(dir_name: str):
        plugins_dir = None
        worlds_dir = None

        if os.path.exists(dir_name + "/plugins"):
            plugins_dir = f"{dir_name}/plugins"

        if os.path.exists(dir_name + "/worlds"):
            worlds_dir = f"{dir_name}/worlds"

        return plugins_dir, worlds_dir

    # :)
    plugins_dir, worlds_dir = get_dirs(
        "./mhdeploytmp/" + os.listdir("./mhdeploytmp")[0])

    if os.path.exists("./plugins"):
        os.remove('./plugins')
    # the most based method of doing it.
    shutil.move(plugins_dir, "./plugins")

    if os.path.exists('./mhdeploytmp'):
        delete_dir("./mhdeploytmp")


def delete_dir(file_path):
    if(os.path.isdir(file_path) == False):
        print(file_path + " is not a directory.")
        return

    for fileName in os.listdir(file_path):
        path = file_path + "/" + fileName

        if os.path.isdir(path):
            delete_dir(path)
            continue

        if os.path.isfile(path):
            delete_file(path)
            continue

    os.rmdir(file_path)


def delete_file(file_path):
    if(os.path.isfile(file_path) == False):
        print(file_path + " is not a file.")
        return

    os.remove(file_path)


class Github:
    api_url = "https://api.github.com"

    def __init__(self, token: str, organization_name: str, repository: str, branch: str):
        self.__token = token
        self._organization_name = organization_name
        self._repository = repository
        self._branch = branch

        self.deployment_file_name = f"mh-deployment-{branch}.tar.gz"

        self.base_headers = {
            "Accept": "application/vnd.github+json",
            "Authorization": "token " + self.__token
        }

    def get_repo_tar(self, chunk_size=1024) -> str:
        res = requests.get(
            self.__handle_replacements(
                self.api_url + "/repos/{OWNER}/{REPO}/tarball/{BRANCH}",
                {
                    "OWNER": self._organization_name,
                    "REPO": self._repository,
                    "BRANCH": self._branch
                }
            ), stream=True, headers=self.base_headers
        )

        raw = res.raw
        with open(self.deployment_file_name, "wb") as dest:
            while True:
                chunk = raw.read(chunk_size, decode_content=True)
                if not chunk:
                    break

                dest.write(chunk)

        return self.deployment_file_name

    def __handle_replacements(self, str: str, replacements: dict) -> str:
        for key, value in replacements.items():
            str = str.replace("{" + key + "}", value)

        return str


if __name__ == "__main__":
    main()
