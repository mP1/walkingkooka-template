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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

import java.util.function.Function;

/**
 * A collection of {@link TemplateContext} factory methods.
 */
public final class TemplateContexts implements PublicStaticHelper {

    /**
     * {@see BasicTemplateContext}
     */
    public static TemplateContext basic(final Function<TextCursor, Template> expressionParser,
                                        final Function<TemplateValueName, Template> nameToTemplate,
                                        final LineEnding lineEnding,
                                        final ExpressionEvaluationContext expressionEvaluationContext) {
        return BasicTemplateContext.with(
                expressionParser,
                nameToTemplate,
                lineEnding,
                expressionEvaluationContext
        );
    }

    /**
     * {@see FakeTemplateContext}
     */
    public static TemplateContext fake() {
        return new FakeTemplateContext();
    }

    /**
     * Stop creation
     */
    private TemplateContexts() {
        throw new UnsupportedOperationException();
    }
}
