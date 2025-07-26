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

import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printer;

import java.util.Set;

public class FakeTemplate implements Template {

    public FakeTemplate() {
        super();
    }

    @Override
    public void render(final Printer printer,
                       final TemplateContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<TemplateValueName> templateValueNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object value() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void printTree(final IndentingPrinter printer) {
        throw new UnsupportedOperationException();
    }
}
