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

    @Override
    public TemplateCollection createTemplate() {
        return TemplateCollection.with(
                Lists.empty()
        );
    }

    @Override
    public TemplateContext createContext() {
        return TemplateContexts.fake();
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
