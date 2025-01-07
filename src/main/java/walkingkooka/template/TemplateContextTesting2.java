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

public interface TemplateContextTesting2<C extends TemplateContext> extends TemplateContextTesting<C> {

    default void templateValueAndCheck(final TemplateValueName name,
                                       final String expected) {
        this.templateValueAndCheck(
                this.createContext(),
                name,
                expected
        );
    }
}
