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

import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void testGetSlashValueParserFails() {
        this.getFails(
            "/${value1}/path2",
            "/BadInteger/path2",
            "value1",
            "Extract ${value1}=BadInteger in /BadInteger/path2, For input string: \"BadInteger\""
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

    @Test
    public void testGetValueMultiple() {
        this.getAndCheck(
            "${value1}/${value2}",
            "1111/2222",
            "value1",
            1111
        );
    }

    @Test
    public void testGetValueMultiple2() {
        this.getAndCheck(
            "${value1}/${value2}",
            "1111/2222",
            "value2",
            2222
        );
    }

    @Test
    public void testGetLastValue() {
        this.getAndCheck(
            "${last}",
            "1111/2222",
            "last",
            (s) -> s,
            "1111/2222"
        );
    }

    @Test
    public void testGetLastValue2() {
        this.getAndCheck(
            "path1/${last}",
            "path1/2222/3333",
            "last",
            (s) -> s,
            "/2222/3333"
        );
    }

    @Test
    public void testGetLastValue3() {
        this.getAndCheck(
            "path1/${last}",
            "path1/2222/3333/4444",
            "last",
            (s) -> s,
            "/2222/3333/4444"
        );
    }

    @Test
    public void testGetLastValueEmpty() {
        this.getAndCheck(
            "path1/${last}",
            "path1/",
            "last",
            (s) -> s,
            "/"
        );
    }

    @Test
    public void testGetLastValueMissingSlash() {
        this.getAndCheck(
            "path1/${last}",
            "path1",
            "last",
            (s) -> s,
            ""
        );
    }

    @Test
    public void testGetLastValueMissingSlash2() {
        this.getAndCheck(
            "path1/path2/${last}",
            "path1/path2",
            "last",
            (s) -> s,
            ""
        );
    }

    @Test
    public void testGetSlashLastValue() {
        this.getAndCheck(
            "/${last}",
            "/1111/2222",
            "last",
            (s) -> s,
            "/1111/2222"
        );
    }

    @Test
    public void testGetSlashLastValue2() {
        this.getAndCheck(
            "/path1/${last}",
            "/path1/2222/3333",
            "last",
            (s) -> s,
            "/2222/3333"
        );
    }

    @Test
    public void testGetSlashLastValue3() {
        this.getAndCheck(
            "/path1/${last}",
            "/path1/2222/3333/4444",
            "last",
            (s) -> s,
            "/2222/3333/4444"
        );
    }

    @Test
    public void testGetSlashLastValueEmpty() {
        this.getAndCheck(
            "/path1/${last}",
            "/path1/",
            "last",
            (s) -> s,
            "/"
        );
    }

    @Test
    public void testGetSlashLastValueMissingSlash() {
        this.getAndCheck(
            "/path1/${last}",
            "/path1",
            "last",
            (s) -> s,
            ""
        );
    }

    @Test
    public void testGetSlashLastValueMissingSlash2() {
        this.getAndCheck(
            "/path1/path2/${last}",
            "/path1/path2",
            "last",
            (s) -> s,
            ""
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
        this.checkEquals(
            this.get(
                template,
                path,
                name
            ),
            expected
        );
    }

    private Optional<Integer> get(final String template,
                                  final String path,
                                  final String name) {
        return UrlPathTemplate.parse(template)
            .tryPrepareValues(
                UrlPath.parse(path)
            ).orElseThrow(
                () -> new IllegalArgumentException("template=" + template + " does not match, Path " + path)
            ).get(
                TemplateValueName.with(name),
                (s) -> Integer.parseInt(
                    s.startsWith("/") ?
                        s.substring(1) :
                        s
                )
            );
    }

    private <T> void getAndCheck(final String template,
                                 final String path,
                                 final String name,
                                 final Function<String, T> parser,
                                 final T expected) {
        this.getAndCheck(
            template,
            path,
            name,
            parser,
            Optional.of(expected)
        );
    }

    private <T> void getAndCheck(final String template,
                                 final String path,
                                 final String name,
                                 final Function<String, T> parser,
                                 final Optional<T> expected) {
        this.getAndCheck(
            UrlPathTemplate.parse(template)
                .tryPrepareValues(UrlPath.parse(path))
                .orElseThrow(() -> new IllegalArgumentException("Path does not match")),
            TemplateValueName.with(name),
            parser,
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

    private void getFails(final String template,
                          final String path,
                          final String name,
                          final String expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.get(
                template,
                path,
                name
            )
        );
        this.checkEquals(
            expected,
            thrown.getMessage(),
            "message"
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
                "    /path1/path2/path3\n"
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
            "template=/path1/${value2}/path3 path=/path1/path2/path3"
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
