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
import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.naming.NameTesting2;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

final public class TemplateValueNameTest implements NameTesting2<TemplateValueName, TemplateValueName>,
        ComparableTesting2<TemplateValueName>,
        JsonNodeMarshallingTesting<TemplateValueName>,
        TreePrintableTesting {

    // parse............................................................................................................

    @Test
    public void testParseWithNullTextCursorFails() {
        assertThrows(
                NullPointerException.class,
                () -> TemplateValueName.parse(null)
        );
    }

    @Test
    public void testParse() {
        final String name = "Hello";
        final TextCursor text = TextCursors.charSequence(name + " 123");

        this.checkEquals(
                Optional.of(TemplateValueName.with(name)),
                TemplateValueName.parse(text)
        );

        final TextCursorSavePoint save = text.save();
        text.end();

        this.checkEquals(
                " 123",
                save.textBetween().toString()
        );
    }

    @Test
    public void testParseNothing() {
        final TextCursor text = TextCursors.charSequence("123");

        this.checkEquals(
                Optional.empty(),
                TemplateValueName.parse(text)
        );

        this.checkEquals(
                '1',
                text.at()
        );
    }

    // with.............................................................................................................

    @Test
    public void testWithInvalidInitialFails() {
        this.withFails(
                "1abc",
                InvalidCharacterException.class,
                "Invalid character '1' at 0"
        );
    }

    @Test
    public void testWithInvalidPartFails() {
        this.withFails(
                "abc$def",
                InvalidCharacterException.class,
                "Invalid character '$' at 3"
        );
    }

    @Test
    public void testWithDotDotFails() {
        this.withFails(
                "abc..def",
                InvalidCharacterException.class,
                "Invalid character '.' at 4"
        );
    }

    private <T extends IllegalArgumentException> void withFails(final String text,
                                                                final Class<T> throwsClass,
                                                                final String message) {
        final T thrown = assertThrows(
                throwsClass,
                () -> TemplateValueName.with(text)
        );

        if (null != message) {
            this.checkEquals(
                    message,
                    thrown.getMessage(),
                    "message"
            );
        }
    }

    @Test
    public void testWith2() {
        this.createNameAndCheck("ZZZ1");
    }

    @Test
    public void testWith3() {
        this.createNameAndCheck("A123Hello");
    }

    @Test
    public void testWith4() {
        this.createNameAndCheck("A1B2C2");
    }

    @Test
    public void testWithLetterDigits() {
        this.createNameAndCheck(
                "A1234567"
        );
    }

    @Test
    public void testWithLetterDigitsLetters() {
        this.createNameAndCheck(
                "A1B"
        );
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkNotEquals(
                TemplateValueName.with("Label123"),
                TemplateValueName.with("LABEL123")
        );
    }

    @Override
    public TemplateValueName createName(final String name) {
        return TemplateValueName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "state";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "postcode";
    }

    @Override
    public int minLength() {
        return 1;
    }

    @Override
    public int maxLength() {
        return TemplateValueName.MAX_LENGTH;
    }

    @Override
    public String possibleValidChars(final int position) {
        return 0 == position ?
                ASCII_LETTERS :
                ASCII_LETTERS_DIGITS + "-.";
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return 0 == position ?
                ASCII_DIGITS + CONTROL + "!@#$%^&*()" :
                CONTROL + "!@#$%^&*()";
    }

    // Comparator ......................................................................................................

    @Test
    public void testSort() {
        final TemplateValueName a1 = TemplateValueName.with("A1");
        final TemplateValueName b2 = TemplateValueName.with("B2");
        final TemplateValueName c3 = TemplateValueName.with("c3");
        final TemplateValueName d4 = TemplateValueName.with("d4");

        this.compareToArraySortAndCheck(
                d4, c3, a1, b2,
                a1, b2, c3, d4
        );
    }

    // toString.........................................................................................................

    @Test
    @Override
    public void testToString() {
        final String value = "Hello123";

        final TemplateValueName name = TemplateValueName.with(value);
        this.toStringAndCheck(
                name,
                name.text()
        );
    }

    // json.............................................................................................................

    private final static String NAME = "template-value-name-123";

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                JsonNode.string(NAME)
        );
    }

    @Override
    public TemplateValueName unmarshall(final JsonNode jsonNode,
                                        final JsonNodeUnmarshallContext context) {
        return TemplateValueName.unmarshall(
                jsonNode,
                context
        );
    }

    @Override
    public TemplateValueName createJsonNodeMarshallingValue() {
        return TemplateValueName.with(NAME);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<TemplateValueName> type() {
        return Cast.to(TemplateValueName.class);
    }
}
