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

package walkingkooka.template;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.expression.Expression;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TemplateCollectionTest implements TemplateTesting2<TemplateCollection> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> TemplateCollection.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        this.checkEquals(
            Templates.string(""),
            TemplateCollection.with(
                Lists.empty()
            )
        );
    }

    @Test
    public void testWithOne() {
        final Template template = Templates.string("Hello123");

        assertSame(
            template,
            TemplateCollection.with(
                Lists.of(template)
            )
        );
    }

    @Test
    public void testWithIncludesTemplateCollection() {
        final Template string111 = Templates.string("111");
        final Template string222 = Templates.string("222");
        final Template string333 = Templates.string("333");

        this.checkEquals(
            TemplateCollection.with(
                Lists.of(
                    string111,
                    string222,
                    string333
                )
            ),
            TemplateCollection.with(
                Lists.of(
                    TemplateCollection.with(
                        Lists.of(
                            string111,
                            string222
                        )
                    ),
                    string333
                )
            )
        );
    }

    // render...........................................................................................................

    @Test
    public void testRenderWhenEmpty() {
        this.renderAndCheck(
                TemplateCollection.with(
                        Lists.empty()
                ),
                TemplateContexts.fake(),
                ""
        );
    }

    @Test
    public void testRenderWhenSeveral() {
        final TemplateValueName name = TemplateValueName.with("name222");

        this.renderAndCheck(
                TemplateCollection.with(
                        Lists.of(
                                Templates.string("111"),
                                Templates.templateValueName(name)
                        )
                ),
                new FakeTemplateContext() {
                    @Override
                    public String templateValue(final TemplateValueName n) {
                        checkEquals(name, n);

                        return "222";
                    }
                },
                "111222"
        );
    }

    // templateValueNames...............................................................................................

    @Test
    public void testTemplateValueNames() {
        final TemplateValueName value1 = TemplateValueName.with("value1");
        final TemplateValueName value2 = TemplateValueName.with("value2");

        this.templateValueNamesAndCheck(
            TemplateCollection.with(
                Lists.of(
                    Templates.string("string1"),
                    Templates.templateValueName(value1),
                    Templates.string("string2"),
                    Templates.templateValueName(value2)
                )
            ),
            value1,
            value2
        );
    }

    @Override
    public TemplateCollection createTemplate() {
        return (TemplateCollection)
            TemplateCollection.with(
                Lists.of(
                    Templates.string("Hello1"),
                    Templates.string("Hello2")
                )
            );
    }

    @Override
    public TemplateContext createContext() {
        return TemplateContexts.fake();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            TemplateCollection.with(
                Lists.of(
                    Templates.string("Hello1"),
                    Templates.templateValueName(
                        TemplateValueName.with("Hello2")
                    ),
                    Templates.expression(
                        Expression.add(
                            Expression.value(1),
                            Expression.value(22)
                        )
                    )
                )
            ),
            "TemplateCollection\n" +
                "  StringTemplate\n" +
                "    \"Hello1\"\n" +
                "  TemplateValueNameTemplate\n" +
                "    ${Hello2}\n" +
                "  ExpressionTemplate\n" +
                "    AddExpression\n" +
                "      ValueExpression 1 (java.lang.Integer)\n" +
                "      ValueExpression 22 (java.lang.Integer)\n"
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentTemplates() {
        this.checkNotEquals(
                TemplateCollection.with(
                        Lists.of(
                                TemplateValueNameTemplate.with(
                                        TemplateValueName.with("different")
                                )
                        )
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                TemplateCollection.with(
                        Lists.of(
                                Templates.string("111"),
                                Templates.string("222")
                        )
                ),
                "111222"
        );
    }

    // class............................................................................................................

    @Override
    public Class<TemplateCollection> type() {
        return TemplateCollection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
