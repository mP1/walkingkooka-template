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

import walkingkooka.NeverError;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.StaticHelper;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;

import java.util.List;
import java.util.Objects;

final class TemplateContextParseTextCursor implements StaticHelper {

    private final static char BRACE_OPEN = '{';
    private final static char DOLLAR_SIGN = '$';
    final static char BRACE_CLOSE = '}';

    private final static int MODE_TEXT = 1;
    private final static int MODE_BACKSLASH = 2;
    private final static int MODE_OPEN_BRACE = 3;

    static Template parse(final TextCursor text,
                          final TemplateContext context) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(context, "context");

        final List<Template> templates = Lists.array();

        int mode = MODE_TEXT;
        final StringBuilder b = new StringBuilder();
        TextCursorLineInfo dollarSignLineInfo = null;

        while (text.isNotEmpty()) {
            final char c = text.at();

            switch (mode) {
                case MODE_TEXT:
                    dollarSignLineInfo = null;

                    switch (c) {
                        case '\\':
                            mode = MODE_BACKSLASH;
                            break;
                        case DOLLAR_SIGN:
                            mode = MODE_OPEN_BRACE;
                            dollarSignLineInfo = text.lineInfo();
                            break;
                        default:
                            b.append(c);
                            break;
                    }
                    text.next();
                    break;
                case MODE_BACKSLASH:
                    b.append(c);
                    text.next();
                    mode = MODE_TEXT;
                    break;
                case MODE_OPEN_BRACE:
                    text.next();

                    addIfNotEmpty(
                            b,
                            templates
                    );

                    switch (c) {
                        case BRACE_OPEN:
                            // ${
                            templates.add(
                                    context.parseTemplateExpression(text)
                            );

                            mode = MODE_TEXT;
                            break;
                        default:
                            throw dollarSignLineInfo.invalidCharacterException()
                                    .get();
                    }
                    break;
                default:
                    throw new NeverError("Invalid mode=" + mode);
            }
        }

        addIfNotEmpty(
                b,
                templates
        );

        return Templates.collection(templates);
    }

    private static void addIfNotEmpty(final StringBuilder b,
                                      final List<Template> templates) {
        if (b.length() > 0) {
            templates.add(
                    Templates.string(
                            b.toString()
                    )
            );
            b.setLength(0);
        }
    }
}
