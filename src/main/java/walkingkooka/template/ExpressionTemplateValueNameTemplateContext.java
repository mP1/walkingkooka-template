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

import walkingkooka.EmptyTextException;
import walkingkooka.InvalidCharacterException;
import walkingkooka.NeverError;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorLineInfo;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link TemplateContext} that supports parsing templates including support for backslash escaped characters, and
 * expressions holding a single {@link TemplateValueName}.
 */
final class ExpressionTemplateValueNameTemplateContext implements TemplateContext {

    static ExpressionTemplateValueNameTemplateContext with(final Function<TemplateValueName, String> nameToValue) {
        return new ExpressionTemplateValueNameTemplateContext(
                Objects.requireNonNull(nameToValue, "nameToValue")
        );
    }

    private ExpressionTemplateValueNameTemplateContext(final Function<TemplateValueName, String> nameToValue) {
        this.nameToValue = nameToValue;
    }

    private final static char BRACE_OPEN = '{';
    private final static char DOLLAR_SIGN = '$';
    private final static char BRACE_CLOSE = '}';

    private final static int MODE_TEXT = 1;
    private final static int MODE_BACKSLASH = 2;
    private final static int MODE_OPEN_BRACE = 3;

    @Override
    public Template parse(final TextCursor text) {
        Objects.requireNonNull(text, "text");

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
                                    this.expression(text)
                            );

                            mode = MODE_TEXT;
                            break;
                        default:
                            throw new InvalidCharacterException(
                                    dollarSignLineInfo.text().toString(),
                                    dollarSignLineInfo.textOffset()
                            );
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

    // consume a {@link TemplateValueName}

    /**
     * Expects a {@link TemplateValueName} followed by a closing brace.
     */
    @Override
    public Template expression(final TextCursor text) {
        Objects.requireNonNull(text, "text");

        final TemplateValueName templateValueName = TEMPLATE_VALUE_NAME_PARSER.parse(text, PARSER_CONTEXT)
                .map(t -> TemplateValueName.with(t.text()))
                .orElseThrow(() -> new EmptyTextException("template value name"));

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Incomplete expression");
        } else {
            final char c = text.at();
            switch (c) {
                case BRACE_CLOSE:
                    text.next();
                    break;
                default:
                    final TextCursorLineInfo lineInfo = text.lineInfo();

                    throw new InvalidCharacterException(
                            lineInfo.text().toString(),
                            lineInfo.textOffset()
                    );
            }
        }

        return Templates.templateValueName(templateValueName);
    }

    private final static Parser<ParserContext> TEMPLATE_VALUE_NAME_PARSER = Parsers.stringInitialAndPartCharPredicate(
            TemplateValueName.INITIAL,
            TemplateValueName.PART,
            1,
            TemplateValueName.MAX_LENGTH
    );

    private final static ParserContext PARSER_CONTEXT = ParserContexts.fake();

    @Override
    public String templateValue(final TemplateValueName name) {
        return this.nameToValue.apply(name);
    }

    private final Function<TemplateValueName, String> nameToValue;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
