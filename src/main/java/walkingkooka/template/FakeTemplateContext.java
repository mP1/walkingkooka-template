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

import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;

import java.util.List;

public class FakeTemplateContext implements TemplateContext {

    public FakeTemplateContext() {
        super();
    }

    @Override
    public Template parseTemplate(final TextCursor text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Template parseTemplateExpression(final TextCursor text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Template templateCollection(final List<Template> templates) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Template templateText(final String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String evaluateAsString(final Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String templateValue(final TemplateValueName name) {
        throw new UnsupportedOperationException();
    }
}
