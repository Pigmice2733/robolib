# RoboLib

RoboLib is reusuable robotics software developed by the Pigmice. It is a Java software library for use with the FRC WPILib platform.

It is intended to be included as a Git submodule and Gradle subproject within a specific robotics project. RoboLib includes a PIDF package, a motion control package, a driver controls package, and many helpful utilities.

## Integration with a project

### Prerequisites

You will need to have Gradle and Git installed. If you do not, instructions can easily be found online.

### Git integration

If you want to be able to continue pulling new updates to RoboLib, or contribute changes back upstream, you will need to add RoboLib as a Git submodule within your project. This also means that you will need to have your project configured as a Git repository. These instructions assume you are familiar with Git and with a terminal - if not, you may want to look at non-Git integration.

To add RoboLib as a Git submodule, run `git submodule add https://github.com/Pigmice2733/robolib.git` within your project. You should see two new staged changes: a file called `.gitmodules` (only if you didn't already have Git submodules added), add `robolib`. Committing these changes will add the submodule and lock RoboLib to the specific version it is at now. If you use git within the `robolib` folder, you are executing Git commands for the RoboLib Git repository, not your project's. If you make changes to RoboLib, or `git pull` new changes within the robolib folder, you should commit those changes to your project to ensure that RoboLib stays locked to the version you are expecting.

From now on, when you clone your project you will need to use `git clone <project-url> --recurse-submodules` rather than just `git clone <project-url>`, and similarly add `--recurse-submodules` when doing `git pull` to fetch changes from RoboLib.

### Non-Git integration

If working with Git submodules seems like a headache you don't want to deal with, you can also simply download a ZIP file of RoboLib from GitHub, and place those files with a `robolib` subfolder with your project.

### Gradle

RoboLib uses Gradle to build dependencies and run tests. If you want to use Gradle for your project, and include RoboLib as a subproject, you will need to have the following in your `settings.gradle`:

```
rootProject.name = '<your project name>'
include 'robolib'
```

You will also need to add the following to the top of the dependencies section of your `build.gradle`:

```
    wpi.deps.vendor.loadFrom(project(':robolib'))

    compile project("robolib")
```

You will need to run `./gradlew build` from within the robolib folder before building your project the first time, or after a `./gradlew clean` or similar. If the build ever seems broken in a way related to RoboLib, try running `./gradlew clean` for your project, then building RoboLib from within the robolib folder, then finally building your project.

For a full example of using RoboLib as a subproject, see [our 2022 FRC competition code](https://github.com/Pigmice2733/frc-2022).

## Contributing

To contribute, simply clone RoboLib like any other Git repo. Then make your changes, add tests if appropriate, make sure all tests still run, and open a PR.
