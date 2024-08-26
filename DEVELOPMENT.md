# Greetings!

It's great to see you being interested in contributing to the project!

#### Here are some steps to get you started:

## Contributing to the project

* Fork the repository
* Clone the repository
* Create a new branch
* Make your changes
* Commit your changes with a descriptive commit message and push your commit to your fork.
* Create a pull request. Please provide a detailed description of your changes and why you think they should be merged.
* Wait for your pull request to be reviewed and merged!

## We would appreciate

* If you are patient and respectful in your interactions with other contributors.
* If you are open to feedback and constructive criticism.

## How to run the project locally

Run a postgresql database on your local machine, with the following credentials:

```
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=pathfinder
```

After you have cloned the repository, navigate to the project directory and run the following command:

```
./start-dev.sh
```

This script copies the necessary environment variables from '.env.prototype'
into a new '.env' file and starts the development server.

#### Voila! The development server should be running at [localhost:8080](http://localhost:8080).

## API documentation

If you're running the server locally, you should be able to access the API documentation
at [here](http://localhost:8080/swagger-ui/index.html).

## I'm new! What issues do I begin with?

Head over to the [issues](https://github.com/mehedikhan72/pathfinder-api/issues) tab and look for issues with the label `good first issue`.
These issues are beginner-friendly and are a great way to get started with contributing to the project.

#### Checkstyle is a great tool to ensure that your code is clean and follows the project's style guide. Unfortunately, it was introduced after the project was started, so there are a lot of violations in the codebase. If you're looking for a simple way to contribute, fixing these violations is a great way to start!

Run the following command to check for violations in your code:

```
mvn checkstyle:check
```

Head over to checktyle's [documentation](https://checkstyle.sourceforge.io/) to learn more about the tool and how to fix
violations.

