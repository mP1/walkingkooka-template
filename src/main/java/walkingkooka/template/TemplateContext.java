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

import walkingkooka.Context;
import walkingkooka.text.printer.Printer;

/**
 * The {@link Context} that handles rendering a template using {@link Template#render(Printer, TemplateContext)}
 */
public interface TemplateContext extends Context {

    /**
     * Resolves the given {@link TemplateValueName} into a {@link String}. When the value is not found,
     * the context can either return an empty string or throw an exception.
     */
    String templateValue(final TemplateValueName name);
}
