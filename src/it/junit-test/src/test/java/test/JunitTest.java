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

/*
 * Copyright © 2025 Miroslav Pokorny
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
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;

import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateContexts;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.printer.Printers;

@J2clTestInput(JunitTest.class)
public class JunitTest {

    @Test
    public void testTrue() {
        Assert.assertEquals(
                true,
                true
        );
    }

    @Test
    public void testParseAndRender() {
        final TemplateContext context = TemplateContexts.expressionTemplateValueName(
                (n) -> "<<" + n.text().toUpperCase() + ">>"
        );

        final StringBuilder b = new StringBuilder();

        context.parseAndRender(
                TextCursors.charSequence("Hello ${abc} 123"),
                Printers.stringBuilder(
                        b,
                        LineEnding.NL
                )
        );

        checkEquals(
                "Hello <<ABC>> 123",
                b.toString()
        );
    }

    private void checkEquals(final String expected,
                             final String actual) {
        Assert.assertEquals(
                expected,
                actual
        );
    }
}
