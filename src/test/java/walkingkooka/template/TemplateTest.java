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
import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;

public final class TemplateTest implements TemplateTesting, ClassTesting<Template> {

    // renderToString...................................................................................................

    @Test
    public void testRenderToString() {
        final TemplateContext context = TemplateContexts.expressionTemplateValueName(
                t -> Templates.templateValueName(
                        TemplateValueName.parse(t)
                                .orElseThrow(() -> new IllegalArgumentException("Missing name"))
                ),
                Maps.of(
                        TemplateValueName.with("abc"),
                        Templates.string("111")
                )::get,
                LineEnding.NL,
                ExpressionEvaluationContexts.fake()
        );

        this.checkEquals(
                "Hello 111 999",
                context.parse(TextCursors.charSequence("Hello ${abc} 999"))
                        .renderToString(
                                LineEnding.NL,
                                context
                        )
        );
    }

    // class............................................................................................................

    @Override
    public Class<Template> type() {
        return Template.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
