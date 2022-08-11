# Minehut Deployment
Minehut deployment is a project designed to allow server owners to not have to give console access to developers in order to update plugins and such, instead they can just push their work to a Github repository and every time the server restarts it will fetch the latest plugin files from Github.

## Implemented Features
* Allow for custom server jar types to be used.
* Deploy plugins from a Github repository.

## Features Planned
* Allow for worlds to be updated from Github.

# Setup
To setup the system install the corresponding plugin to the server you're trying to deploy on.

Example being if you're attempting to deploy on a Velocity Proxy install the Velocity plugin and setup a file in your root directory as follows.

Setup a `.deployment_information` file with the following fields, it will look similar to a `.env` file.
````
token=
organization=
repository=
branch=
webhook_url=
server_name=
jar_name=
````
* `token=` is your Github authentication token
* `organization=` is either the organization the repository belongs to or your personal user account.
* `repository=` is the repository to use from the organization.
* `branch=` is the branch it will use to find the plugins
* `webhook_url=` is a discord webhook url to use if you want notifications of when a deployment is made.
* `server_name=` is the name of the server, it's only ever used with discord webhook and is not required.
* `jar_name=` is the name of the custom server jar you'd like to use, you just put it in the root directory. **Please do not use the name server.jar.**

In your Github repository make a directory called `plugins` and just put your plugin jars and folders in there and Minehut Deployment will take care of the rest.

Feel free to leave a star to help support me.