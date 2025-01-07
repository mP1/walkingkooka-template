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

import walkingkooka.InvalidCharacterException;
import walkingkooka.InvalidTextLengthException;
import walkingkooka.compare.Comparators;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.Objects;
import java.util.Optional;

/**
 * The name of an template variable value. Names must start with a letter, followed by letters/digits/dash and are case-sensitive.
 */
final public class TemplateValueName implements Name, Comparable<TemplateValueName> {

    /**
     * Names must start with a letter.
     */
    public final static CharPredicate INITIAL = CharPredicates.letter();

    /**
     * Valid characters for characters following the first, which may be a letter, digit or dash.
     */
    public final static CharPredicate PART = INITIAL.or(
            CharPredicates.range('0', '9') // numbers
    ).or(
            CharPredicates.any("-.")
    );

    /**
     * The maximum valid length for a environment value name.
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Makes a best effort to try and parse a {@link TemplateValueName} advancing the {@link TextCursor}.
     */
    public static Optional<TemplateValueName> parse(final TextCursor text) {
        Objects.requireNonNull(text, "text");

        return PARSER.parse(text, PARSER_CONTEXT)
                .map(t -> TemplateValueName.with(t.text()));
    }

    private final static Parser<ParserContext> PARSER = Parsers.stringInitialAndPartCharPredicate(
            TemplateValueName.INITIAL,
            TemplateValueName.PART,
            1,
            TemplateValueName.MAX_LENGTH
    );

    private final static ParserContext PARSER_CONTEXT = ParserContexts.fake();
    
    /**
     * Factory that creates a {@link TemplateValueName}
     */
    public static TemplateValueName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
                name,
                "name",
                INITIAL,
                PART
        );

        if (name.length() >= MAX_LENGTH) {
            throw new InvalidTextLengthException("name", name, 0, MAX_LENGTH);
        }

        final int dotdot = name.indexOf("..");
        if (-1 != dotdot) {
            throw new InvalidCharacterException(name, 1 + dotdot);
        }

        return new TemplateValueName(name);
    }

    /**
     * Private constructor
     */
    private TemplateValueName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    // Comparable........................................................................................................

    @Override
    public int compareTo(final TemplateValueName other) {
        return CASE_SENSITIVITY.comparator().compare(this.name, other.name);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof TemplateValueName && this.equals0((TemplateValueName) other);
    }

    private boolean equals0(final TemplateValueName other) {
        return this.compareTo(other) == Comparators.EQUAL;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // HasCaseSensitivity...............................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    public final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;
}
