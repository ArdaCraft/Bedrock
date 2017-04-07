/**
 * This file is part of Bedrock, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.bedrock.data.invincibility;

import com.helion3.bedrock.util.BedrockKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableInvincibilityData extends AbstractImmutableData<ImmutableInvincibilityData, InvincibilityData> {
    private boolean invincible;

    public ImmutableInvincibilityData(boolean invincible) {
        this.invincible = invincible;
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(BedrockKeys.IS_INVINCIBLE, this::isInvincible);
        registerKeyValue(BedrockKeys.IS_INVINCIBLE, this::initialBool);
    }

    public ImmutableValue<Boolean> initialBool() {
        return Sponge.getRegistry().getValueFactory().createValue(BedrockKeys.IS_INVINCIBLE, false, this.invincible).asImmutable();
    }

    public boolean isInvincible() {
        return invincible;
    }

    @Override
    public InvincibilityData asMutable() {
        return new InvincibilityData(this.invincible);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }
}
