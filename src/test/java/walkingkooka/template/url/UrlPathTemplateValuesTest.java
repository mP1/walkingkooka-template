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
import walkingkooka.ToStringTesting;
import walkingkooka.net.UrlPath;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.function.Function;

public final class UrlPathTemplateValuesTest implements TreePrintableTesting,
    ToStringTesting<UrlPathTemplateValues>,
    ClassTesting<UrlPathTemplateValues> {

    @Test
    public void testGetSlashValueMissing() {
        this.getAndCheck(
            "/path1/path2",
            "/path1/path2",
            "missing"
        );
    }

    @Test
    public void testGetValueMissing() {
        this.getAndCheck(
            "path1/path2",
            "path1/path2",
            "missing"
        );
    }

    @Test
    public void testGetSlashValueAtStart() {
        this.getAndCheck(
            "/${value1}",
            "/111",
            "value1",
            111
        );
    }

    @Test
    public void testGetSlashValueAtStart2() {
        this.getAndCheck(
            "/${value1}/path2",
            "/111/path2",
            "value1",
            111
        );
    }

    @Test
    public void testGetSlashValueAtEndThenSlash() {
        this.getAndCheck(
            "/path1/${value2}/",
            "/path1/222/",
            "value2",
            222
        );
    }

    @Test
    public void testGetSlashValueAtEnd() {
        this.getAndCheck(
            "/path1/${value2}",
            "/path1/2222",
            "value2",
            2222
        );
    }

    @Test
    public void testGetValueAtStart() {
        this.getAndCheck(
            "${value1}",
            "111",
            "value1",
            111
        );
    }

    @Test
    public void testGetValueAtStart2() {
        this.getAndCheck(
            "${value1}/path2",
            "111/path2",
            "value1",
            111
        );
    }

    @Test
    public void testGetValueAtEndThenSlash() {
        this.getAndCheck(
            "path1/${value2}/",
            "path1/222/",
            "value2",
            222
        );
    }

    @Test
    public void testGetValueAtEnd() {
        this.getAndCheck(
            "path1/${value2}",
            "path1/2222",
            "value2",
            2222
        );
    }

    private void getAndCheck(final String template,
                             final String path,
                             final String name) {
        this.getAndCheck(
            template,
            path,
            name,
            Optional.empty()
        );
    }

    private void getAndCheck(final String template,
                             final String path,
                             final String name,
                             final Integer expected) {
        this.getAndCheck(
            template,
            path,
            name,
            Optional.of(expected)
        );
    }

    private void getAndCheck(final String template,
                             final String path,
                             final String name,
                             final Optional<Integer> expected) {
        this.getAndCheck(
            UrlPathTemplate.parse(template)
                .tryPrepareValues(
                    UrlPath.parse(path)
                ).orElseThrow(() -> new IllegalArgumentException("template=" + template + " does not match, Path " + path)),
            TemplateValueName.with(name),
            Integer::parseInt,
            expected
        );
    }

    private <T> void getAndCheck(final UrlPathTemplateValues values,
                                 final TemplateValueName name,
                                 final Function<String, T> parser,
                                 final Optional<T> expected) {
        this.checkEquals(
            expected,
            values.get(
                name,
                parser
            ),
            () -> "values " + name + " " + parser
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            UrlPathTemplate.parse("/path1/${value2}/path3")
                .tryPrepareValues(
                    UrlPath.parse("/path1/path2/path3")
                ),
            "UrlPathTemplateValues\n" +
                "  template\n" +
                "    TemplateCollection\n" +
                "      StringTemplate\n" +
                "        \"/\"\n" +
                "      StringTemplate\n" +
                "        \"path1\"\n" +
                "      StringTemplate\n" +
                "        \"/\"\n" +
                "      TemplateValueNameTemplate\n" +
                "        ${value2}\n" +
                "      StringTemplate\n" +
                "        \"/\"\n" +
                "      StringTemplate\n" +
                "        \"path3\"\n" +
                "  components\n" +
                "    \"/\"\n" +
                "    \"path1\"\n" +
                "    \"/\"\n" +
                "    value2\n" +
                "    \"/\"\n" +
                "    \"path3\"\n" +
                "  path\n" +
                "    [, path1, path2, path3]\n"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            UrlPathTemplate.parse("/path1/${value2}/path3")
                .tryPrepareValues(
                    UrlPath.parse("/path1/path2/path3")
                ).get(),
            "template=/path1/${value2}/path3 path=[, path1, path2, path3]"
        );
    }

    // class............................................................................................................

    @Override
    public Class<UrlPathTemplateValues> type() {
        return UrlPathTemplateValues.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
