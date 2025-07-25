/*
 * Copyright 2025 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.template.url;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.UrlPath;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.template.FakeTemplateContext;
import walkingkooka.template.Template;
import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateContexts;
import walkingkooka.template.TemplateTesting2;
import walkingkooka.template.TemplateValueName;
import walkingkooka.template.Templates;
import walkingkooka.test.ParseStringTesting;

import java.util.Optional;
import java.util.function.Function;

public final class UrlPathTemplateTest implements TemplateTesting2<UrlPathTemplate>,
    ParseStringTesting<UrlPathTemplate>,
    HashCodeEqualsDefinedTesting2<UrlPathTemplate> {

    // parseString......................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseWithDollarFails() {
        this.parseStringFails(
            "$",
            new IllegalStateException("Incomplete value name")
        );
    }

    @Test
    public void testParseWithDollarBraceFails() {
        this.parseStringFails(
            "${",
            new IllegalStateException("Incomplete value name")
        );
    }

    @Test
    public void testParseIncompleteValueNameFails() {
        this.parseStringFails(
            "${Hello",
            new IllegalStateException("Incomplete value name")
        );
    }

    @Test
    public void testParseWithEmptyPath() {
        this.parseTemplateAndCheck(
            "",
            UrlPathTemplateTemplateContext.EMPTY
        );
    }

    @Test
    public void testParseWithRoot() {
        this.parseTemplateAndCheck(
            "/",
            UrlPathTemplateTemplateContext.SEPARATOR
        );
    }

    @Test
    public void testParseWithRootPathComponent() {
        this.parseTemplateAndCheck(
            "/hello",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("hello")
        );
    }

    @Test
    public void testParseWithRootPathComponent2() {
        this.parseTemplateAndCheck(
            "/hello1/zebra2",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("hello1"),
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("zebra2")
        );
    }

    @Test
    public void testParseWithRootPathComponent3() {
        this.parseTemplateAndCheck(
            "/hello1/banana2/zebra3",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("hello1"),
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("banana2"),
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("zebra3")
        );
    }

    @Test
    public void testParseWithRootPathComponent3Separator() {
        this.parseTemplateAndCheck(
            "/hello1/banana2/zebra3/",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("hello1"),
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("banana2"),
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("zebra3"),
            UrlPathTemplateTemplateContext.SEPARATOR
        );
    }

    @Test
    public void testParseWithRootPathComponentTrailingSlash() {
        this.parseTemplateAndCheck(
            "/hello/",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("hello"),
            UrlPathTemplateTemplateContext.SEPARATOR
        );
    }

    @Test
    public void testParseWithValueName() {
        this.parseTemplateAndCheck(
            "${value1}",
            Templates.templateValueName(
                TemplateValueName.with("value1")
            )
        );
    }

    @Test
    public void testParseWithRootThenValueName() {
        this.parseTemplateAndCheck(
            "/${value1}",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.templateValueName(
                TemplateValueName.with("value1")
            )
        );
    }

    @Test
    public void testParseWithRootThenValueNameTrailingSlash() {
        this.parseTemplateAndCheck(
            "/${value1}/",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.templateValueName(
                TemplateValueName.with("value1")
            ),
            UrlPathTemplateTemplateContext.SEPARATOR
        );
    }

    @Test
    public void testParseWithRootValueNameText() {
        this.parseTemplateAndCheck(
            "/${value1}/text2",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.templateValueName(
                TemplateValueName.with("value1")
            ),
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("text2")
        );
    }

    @Test
    public void testParseWithRootValueNameTextSlash() {
        this.parseTemplateAndCheck(
            "/${value1}/text2/",
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.templateValueName(
                TemplateValueName.with("value1")
            ),
            UrlPathTemplateTemplateContext.SEPARATOR,
            Templates.string("text2"),
            UrlPathTemplateTemplateContext.SEPARATOR
        );
    }

    private void parseTemplateAndCheck(final String text,
                                       final Template... templates) {
        this.parseStringAndCheck(
            text,
            new UrlPathTemplate(
                Templates.collection(
                    Lists.of(templates)
                )
            )
        );
    }

    @Override
    public UrlPathTemplate parseString(final String text) {
        return UrlPathTemplate.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // render...........................................................................................................

    @Test
    public void testRenderWithPathStartsWithSlash() {
        this.renderAndCheck(
            this.parseString("/path1/${value2}/path3/${value4}/path5"),
            new FakeTemplateContext() {
                @Override
                public String templateValue(final TemplateValueName name) {
                    return name.value().toUpperCase() + name.value().toUpperCase();
                }
            },
            "/path1/VALUE2VALUE2/path3/VALUE4VALUE4/path5"
        );
    }

    @Test
    public void testRenderWithPathMissingSlash() {
        this.renderAndCheck(
            this.parseString("path1/${value2}/path3/${value4}/path5"),
            new FakeTemplateContext() {
                @Override
                public String templateValue(final TemplateValueName name) {
                    return name.value().toUpperCase() + name.value().toUpperCase();
                }
            },
            "path1/VALUE2VALUE2/path3/VALUE4VALUE4/path5"
        );
    }

    // renderPath.......................................................................................................

    @Test
    public void testRenderPathWithPathStartsWithSlash() {
        this.renderPathAndCheck(
            "/path1/${value2}/path3/${value4}/path5",
            (name) -> name.value().toUpperCase() + name.value().toUpperCase(),
            "/path1/VALUE2VALUE2/path3/VALUE4VALUE4/path5"
        );
    }

    @Test
    public void testRenderPathWithPathMissingSlash() {
        this.renderPathAndCheck(
            "path1/${value2}/path3/${value4}/path5",
            (name) -> name.value().toUpperCase() + name.value().toUpperCase(),
            "path1/VALUE2VALUE2/path3/VALUE4VALUE4/path5"
        );
    }

    private void renderPathAndCheck(final String template,
                                    final Function<TemplateValueName, String> nameToValue,
                                    final String expected) {
        this.renderPathAndCheck(
            this.parseString(template),
            nameToValue,
            UrlPath.parse(expected)
        );
    }

    private void renderPathAndCheck(final UrlPathTemplate template,
                                    final Function<TemplateValueName, String> nameToValue,
                                    final UrlPath expected) {
        this.checkEquals(
            expected,
            template.renderPath(nameToValue),
            template::toString
        );
    }

    // tryPrepareValues.................................................................................................

    private final static String PATH_SEPARATOR = UrlPath.SEPARATOR.string();

    private final static TemplateValueName VALUE1 = TemplateValueName.with("value1");

    private final static TemplateValueName VALUE2 = TemplateValueName.with("value2");

    private final static TemplateValueName VALUE3 = TemplateValueName.with("value3");

    @Test
    public void testTryPrepareValuesWithEmptyTemplateAndEmptyPath() {
        this.tryPrepareValuesAndCheck(
            "",
            ""
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashDifferentComponent() {
        this.tryPrepareValuesAndCheck(
            "/path",
            "/different"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashComponentDifferentComponent() {
        this.tryPrepareValuesAndCheck(
            "/path1/path2",
            "/path1/different"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashComponentDifferentComponentComponent() {
        this.tryPrepareValuesAndCheck(
            "/path1/path2/path3",
            "/path1/different/path3"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashTemplateIncludesMore() {
        this.tryPrepareValuesAndCheck(
            "/path1/path2/path3",
            "/"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashTemplateIncludesMore2() {
        this.tryPrepareValuesAndCheck(
            "/path1/path2/path3",
            "/path1/path2"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashValue() {
        this.tryPrepareValuesAndCheck(
            "/${value1}",
            "/path1",
            PATH_SEPARATOR,
            VALUE1
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashValueComponent() {
        this.tryPrepareValuesAndCheck(
            "/${value1}/path2",
            "/path1/path2",
            PATH_SEPARATOR,
            VALUE1,
            PATH_SEPARATOR,
            "path2"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashComponentValue() {
        this.tryPrepareValuesAndCheck(
            "/path1/${value2}",
            "/path1/path2",
            PATH_SEPARATOR,
            "path1",
            PATH_SEPARATOR,
            VALUE2
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashComponentValueComponent() {
        this.tryPrepareValuesAndCheck(
            "/path1/${value2}/path3",
            "/path1/path2/path3",
            PATH_SEPARATOR,
            "path1",
            PATH_SEPARATOR,
            VALUE2,
            PATH_SEPARATOR,
            "path3"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashComponentValueExtraComponent() {
        this.tryPrepareValuesAndCheck(
            "/path1/${value2}",
            "/path1/path2/path3",
            PATH_SEPARATOR,
            "path1",
            PATH_SEPARATOR,
            VALUE2
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashComponentValueValueSlash() {
        this.tryPrepareValuesAndCheck(
            "/path1/${value2}/${value3}/",
            "/path1/path2/path3"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathSlashComponentValueValue() {
        this.tryPrepareValuesAndCheck(
            "/path1/${value2}/${value3}",
            "/path1/path2/",
            PATH_SEPARATOR,
            "path1",
            PATH_SEPARATOR,
            VALUE2,
            PATH_SEPARATOR,
            VALUE3
        );
    }

    @Test
    public void testTryPrepareValuesWithTemplateMissingSlashPathWithSlash() {
        this.tryPrepareValuesAndCheck(
            "path",
            "/path"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathComponentDifferentComponent() {
        this.tryPrepareValuesAndCheck(
            "path1/path2",
            "path1/different"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathComponentDifferentComponentComponent() {
        this.tryPrepareValuesAndCheck(
            "path1/path2/path3",
            "path1/different/path3"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathMissingTemplateIncludesMore() {
        this.tryPrepareValuesAndCheck(
            "path1/path2/path3",
            ""
        );
    }

    @Test
    public void testTryPrepareValuesWithPathMissingTemplateIncludesMore2() {
        this.tryPrepareValuesAndCheck(
            "path1/path2/path3",
            "path1/path2"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathValue() {
        this.tryPrepareValuesAndCheck(
            "${value1}",
            "path1",
            VALUE1
        );
    }

    @Test
    public void testTryPrepareValuesWithPathValueComponent() {
        this.tryPrepareValuesAndCheck(
            "${value1}/path2",
            "path1/path2",
            VALUE1,
            PATH_SEPARATOR,
            "path2"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathComponentValue() {
        this.tryPrepareValuesAndCheck(
            "path1/${value2}",
            "path1/path2",
            "path1",
            PATH_SEPARATOR,
            VALUE2
        );
    }

    @Test
    public void testTryPrepareValuesWithPathComponentValueComponent() {
        this.tryPrepareValuesAndCheck(
            "path1/${value2}/path3",
            "path1/path2/path3",
            "path1",
            PATH_SEPARATOR,
            VALUE2,
            PATH_SEPARATOR,
            "path3"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathComponentValueComponentExtraComponent() {
        this.tryPrepareValuesAndCheck(
            "path1/${value2}",
            "path1/path2/path3",
            "path1",
            PATH_SEPARATOR,
            VALUE2
        );
    }

    @Test
    public void testTryPrepareValuesWithPathComponentValueComponentSlash() {
        this.tryPrepareValuesAndCheck(
            "/path1/${value2}/${value3}/",
            "/path1/path2/path3"
        );
    }

    @Test
    public void testTryPrepareValuesWithPathComponentValueValue() {
        this.tryPrepareValuesAndCheck(
            "path1/${value2}/${value3}",
            "path1/path2/",
            "path1",
            PATH_SEPARATOR,
            VALUE2,
            PATH_SEPARATOR,
            VALUE3
        );
    }
    private void tryPrepareValuesAndCheck(final String template,
                                          final String path) {
        this.tryPrepareValuesAndCheck(
            UrlPathTemplate.parse(template),
            UrlPath.parse(path)
        );
    }

    private void tryPrepareValuesAndCheck(final UrlPathTemplate template,
                                          final UrlPath path) {
        this.tryPrepareValuesAndCheck(
            template,
            path,
            Optional.empty()
        );
    }

    private void tryPrepareValuesAndCheck(final String template,
                                          final String path,
                                          final Object... expected) {
        this.tryPrepareValuesAndCheck(
            UrlPathTemplate.parse(template),
            UrlPath.parse(path),
            expected
        );
    }

    private void tryPrepareValuesAndCheck(final String template,
                                          final String path,
                                          final UrlPathTemplateValues expected) {
        this.tryPrepareValuesAndCheck(
            UrlPathTemplate.parse(template),
            UrlPath.parse(path)
        );
    }

    private void tryPrepareValuesAndCheck(final UrlPathTemplate template,
                                          final UrlPath path,
                                          final Object... expected) {
        this.tryPrepareValuesAndCheck(
            template,
            path,
            UrlPathTemplateValues.with(
                template.template,
                Lists.of(expected),
                path,
                path.namesList()
            )
        );
    }

    private void tryPrepareValuesAndCheck(final UrlPathTemplate template,
                                          final UrlPath path,
                                          final UrlPathTemplateValues expected) {
        this.tryPrepareValuesAndCheck(
            template,
            path,
            Optional.of(expected)
        );
    }

    private void tryPrepareValuesAndCheck(final UrlPathTemplate template,
                                          final UrlPath path,
                                          final Optional<UrlPathTemplateValues> expected) {
        this.checkEquals(
            expected,
            template.tryPrepareValues(path),
            () -> template + " tryPrepareValues " + path
        );
    }

    @Override
    public UrlPathTemplate createTemplate() {
        return new UrlPathTemplate(
            Templates.string("HelloWorld")
        );
    }

    @Override
    public TemplateContext createContext() {
        return TemplateContexts.fake();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            new UrlPathTemplate(
                Templates.string("Different")
            )
        );
    }

    @Override
    public UrlPathTemplate createObject() {
        return new UrlPathTemplate(
            Templates.string("Hello")
        );
    }

    // Object...........................................................................................................

    @Override
    public Class<UrlPathTemplate> type() {
        return UrlPathTemplate.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
