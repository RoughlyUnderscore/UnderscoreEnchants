# Contributing to UnderscoreEnchants

First and foremost, thanks for taking your time to contribute! ❤️

Any type of contribution is welcome and valued. UnderscoreEnchants has got a list of guidelines to be followed whilst contributing for the sake of easier maintenance of
the project. Take a look at the **[table o' contents](#table-o-contents)** for reference. The community looks forward to your contributions 😊

Please note that the baseline for all contributions is the [Code of Conduct](./CODE_OF_CONDUCT.md).

# Table o' contents

- **[How to contribute](#how-to-contribute)**
- **[How to suggest a feature](#how-to-suggest-a-feature)**
- **[How to report a bug](#how-to-report-a-bug)**
- **[Code styling](#code-styling)**
- **[Join the development team](#join-the-development-team)**

How to contribute
===
Once again, thanks for being with us! If you are here to contribute any code, extra documentation, or anything else, this paragraph is for you.

### Expectations
You should not be contributing if you have nothing to bring to the table. Here are the general guidelines:

###### What's welcome

1) Making a contribution that only focuses on fixing code style is **not welcome**.
2) Making a contribution that only optimizes something is **not welcome**.
3) Making a contribution that only adds unit tests is **not welcome**.
4) Making a contribution that adds/removes an easter egg is **not welcome**.
5) Making a contribution that largely refactors a big part of the code is **frowned upon, but not forbidden**. However, care to document the new code structure in the commit message for it to have a chance to be accepted.

###### What isn't welcome

1) Making a contribution that fixes a bug is **welcome**.
2) Making a contribution that adds a new API method or fixes its implementation is **welcome**.
3) Making a contribution that adds a feature is **welcome**.
4) Making a contribution that refactors a minor but tedious part of the code (such as removing a clearly redundant variable in 100 places) is **welcome**.

###### What should every contribution have

1) If a new feature can have a unit test, it should have a unit test. This also applies to API implementations.
2) If a new feature can be optimized, it should be optimized. This also applies to API implementations.
3) If a new feature can be written in Kotlin, it should be written in Kotlin. While Java code is still welcome, the project is slowly moving to fully Kotlin.
4) If the new code is untested (including any unit tests failing), it will not be accepted.

### Requirements
* `Java 19`
### Creating a PR
This project relies on the Git VCS for convenience. As such, you're expected to be familiar with it and how to create PRs.
### Legal notice
Developer Certificate of Origin, version 1.1
```
Developer Certificate of Origin
Version 1.1

Copyright (C) 2004, 2006 The Linux Foundation and its contributors.

Everyone is permitted to copy and distribute verbatim copies of this
license document, but changing it is not allowed.


Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or

(b) The contribution is based upon previous work that, to the best
    of my knowledge, is covered under an appropriate open source
    license and I have the right under that license to submit that
    work with modifications, whether created in whole or in part
    by me, under the same open source license (unless I am
    permitted to submit under a different license), as indicated
    in the file; or

(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.

(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including all
    personal information I submit with it, including my sign-off) is
    maintained indefinitely and may be redistributed consistent with
    this project or the open source license(s) involved.
```
### Commit message
A commit message must be fairly small, yet informative.
1) The first line should be the general commit idea.
2) The second line should be left empty.
3) Everything else should be wrapped to not be longer than 90 characters - if it is, put it on a new line (except for URLs).
4) If the commit message has any general links or such information (such as "References"), they should be put on the last lines.

Example of a good commit message:
```
sum: Adding a new trigger

A new trigger, "PLAYERLEAVEBED", has been added. This will allow for users detect
players leaving their beds. Can be useful for some armor enchantments.
Aliases:
- PLAYERLEAVEBED
- PLAYERBEDLEAVE
- WAKEUP
- PLAYERWAKEUP
- GETUP
- PLAYERGETUP

Reference: issue #52 (request: add player leave bed)
```

How to suggest a feature
===
Suggesting features is also contribution in a way! That said, there are some guidelines for suggesting features.
### Where to suggest features
You can suggest features in [GitHub Issues](https://github.com/RoughlyUnderscore/UnderscoreEnchantsCode/issues). If you do not feel like it,
you are always welcome to write at #suggestions in my [Support Discord server](https://discord.gg/bBge7bj3ra).
### A good feature suggestion
What makes a good feature suggestion is the research behind it.
1) Make sure that there's no (recent) issue already open on that topic.
2) If you're going to use Discord, take a look at the last couple messages in #suggestions, because maybe such feature has been proposed recently.
3) Really make sure that the feature isn't already there. For example, there's no reason for an "expect-all-fail" condition flag, because conditions
can be negated.

How to report a bug
===
If it's a minor bug, report it in [GitHub Issues](https://github.com/RoughlyUnderscore/UnderscoreEnchantsCode/issues). However, if it's a major exploitable
bug or a security exploit, refrain from using issues, and instead open a ticket in my [Support Discord server](https://discord.gg/bBge7bj3ra). Before opening
an issue, please also make sure that there's no (recent) issue already open on that bug.

Code styling
===
Are you ready to make your first contribution to UnderscoreEnchants? Well then, have at it! While writing code, please make sure to follow some code styling code
guidelines.

1) UnderscoreEnchants code is [K&R-styled](https://en.wikipedia.org/wiki/Indentation_style#K&R_style). There are some minor adjustments:
```java
// For short one-line expressions, this is acceptable, but not preferred
if (1 == 2)
  return;

// Instead, this is preferred
if (1 == 2) return;

// However, for long one-line expressions, this is acceptable, but not preferred
if (1 == 2) bake().cook().doSomethingElse().extraLong().maybe().optional().getOrElse(throw new RuntimeException());

// Instead, this is preferred
if (1 == 2)
  bake().cook().doSomethingElse().extraLong().maybe().optional().getOrElse(throw new RuntimeException());
```

2) Not every single method should be documented, but if the name isn't clear enough, it should be. Moreover, any method in a utility class (such as Utils, ActionUtils)
should be documented.
3) If a variable can be final, it should be.
4) If a Lombok annotation can be used, it should be (exceptions: @SneakyThrows, reason - Kotlin doesn't like it).
5) The code indentation is **2 spaces** for Kotlin/Java and **4 spaces** for XML!

Join the development team
===
As of update 2.1, the plugin is developed solely by RoughlyUnderscore. As such, if you're ready to dedicate time and patience to this project, contact me via a ticket
in my [support Discord server](https://discord.gg/bBge7bj3ra) and we'll figure something out!
