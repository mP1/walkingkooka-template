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

import walkingkooka.reflect.PublicStaticHelper;

/**
 * A collection of {@link Template} factory methods.
 */
public final class Templates implements PublicStaticHelper {

    /**
     * {@see StringTemplate}
     */
    public static Template string(final String text) {
        return StringTemplate.with(text);
    }

    /**
     * Stop creation
     */
    private Templates() {
        throw new UnsupportedOperationException();
    }
}
