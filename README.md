[![Build Status](https://github.com/mP1/walkingkooka-template/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-template/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-template/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-template?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-template.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-template/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-template.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-template/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-template)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

# walkingkooka-template

A templating engine that supports many simple and advanced features expected and not present in other templating
systems.

Templating consists of 2 distinct phases:

- compiling
- rendering

A [TemplateContext](https://github.com/mP1/walkingkooka-template/blob/master/src/main/java/walkingkooka/template/TemplateContext.java)
provide supports a pluggable provider for both phases.

The sample below, shows several features

- Raw text
- A template variable
- Invoking a function passing parameters. More will be discussed below on possible features.

```
Hello ${user-name}, 
the sum of 1, 2, 3 is ${sum(1, 2, 3}.
```

which renders something like

```
Hello Miroslav,
the sum of 1, 2, 3 is 6.
```

## Compiling

Parses a `String` which includes the raw text and placeholders holding expressions into
a [Template](https://github.com/mP1/walkingkooka-template/blob/master/src/main/java/walkingkooka/template/Template.java).

This supports several positive benefits

- Compile once and cache for multiple renders
- Syntactical validation of any expressions within placeholders.
- Templates are immutable and may be cached for rendering in multiple threads an unlimited number of times.

## Rendering

Renders a `Template` to text using the provided `TemplateContext`.

- Provides a formatted value for any
  given [TemplateValueName](https://github.com/mP1/walkingkooka-template/blob/master/src/main/java/walkingkooka/template/TemplateValueName.java)
  that appears.
- Unknown `TemplateValueName` may be handled in many ways
    - Return an empty string
    - Render some other default string
    - throw an exception, reporting the problem.
- Expressions within templates may or may not include numbers, math expressions or functions, the exact features depends
  on the `TemplateContext` implementation.

## TemplateContext

A `TemplateContext` supports both phases of compiling and rendering. Below is an example that may be studied.

[SpreadsheetTemplateContextTemplateContext](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/template/SpreadsheetTemplateContextTemplateContext.java)
is an advanced context that leverages the Spreadsheet Formula expression system allowing expressions similar to those
that appear within spreadsheet cells.
Cell and label references are removed and replaced by `TemplateValueName(s)`. It is also possible to invoke the many
supported spreadsheet functions including user supplied implementations with templates.

The [SpreadsheetTemplateContextTemplateContextTest](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/test/java/walkingkooka/spreadsheet/template/SpreadsheetTemplateContextTemplateContextTest.java)
contains many tests that may be referenced as examples of a template with placeholders and expressions and the final
expected rendered result.

