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
import walkingkooka.template.url.UrlPathTemplate;
import walkingkooka.text.LineEnding;

public final class RenderOnlyTemplateContextTest implements TemplateContextTesting<RenderOnlyTemplateContext> {

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testRender() {
        final Template template = UrlPathTemplate.parse("/api/spreadsheet/${SpreadsheetId}/cell/${cell}");

        this.checkEquals(
            "/api/spreadsheet/1/cell/A1",
            template.renderToString(
                LineEnding.NL,
                RenderOnlyTemplateContext.with(
                    (TemplateValueName n) -> {
                        switch (n.value()) {
                            case "SpreadsheetId":
                                return "1";
                            case "cell":
                                return "A1";
                            default:
                                throw new AssertionError("Unknown template: " + n);
                        }
                    }
                )
            )
        );
    }

    @Override
    public RenderOnlyTemplateContext createContext() {
        return RenderOnlyTemplateContext.with(
            (TemplateValueName value) -> "" + value.value() + value.value()
        );
    }

    // class............................................................................................................

    @Override
    public Class<RenderOnlyTemplateContext> type() {
        return RenderOnlyTemplateContext.class;
    }
}
