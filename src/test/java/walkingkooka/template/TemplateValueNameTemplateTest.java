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
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TemplateValueNameTemplateTest implements TemplateTesting2<TemplateValueNameTemplate> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> TemplateValueNameTemplate.with(null)
        );
    }

    // render...........................................................................................................

    @Test
    public void testRender() {
        final TemplateValueName name = TemplateValueName.with("Hello");
        final String text = "Goodbye";

        this.renderAndCheck(
                TemplateValueNameTemplate.with(name),
                new FakeTemplateContext() {
                    @Override
                    public String templateValue(final TemplateValueName n) {
                        checkEquals(name, n);

                        return text;
                    }
                },
                text
        );
    }

    @Override
    public TemplateValueNameTemplate createTemplate() {
        return TemplateValueNameTemplate.with(
                TemplateValueName.with("Hello")
        );
    }

    @Override
    public TemplateContext createContext() {
        return TemplateContexts.fake();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsTemplateValueName() {
        this.checkNotEquals(
                TemplateValueNameTemplate.with(
                        TemplateValueName.with("different")
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createTemplate(),
                "${Hello}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
            TemplateValueNameTemplate.with(
                TemplateValueName.with("Hello")
            ),
            "TemplateValueNameTemplate\n" +
                "  Hello\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<TemplateValueNameTemplate> type() {
        return TemplateValueNameTemplate.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
