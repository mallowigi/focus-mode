/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 ******************************************************************************/

package com.mallowigi.focusmode;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.SettingsCategory;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.ui.ColorUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.Transient;
import com.mallowigi.focusmode.config.MTBaseConfig;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;


@SuppressWarnings({
  "WeakerAccess",
  "PackageVisibleField",
  "MethodReturnOfConcreteClass",
  "StaticMethodOnlyUsedInOneClass",
  "TransientFieldInNonSerializableClass",
  "ParameterHidesField",
  "java:S1820"
})
@State(
  name = "FocusModeConfig", //NON-NLS
  storages = @Storage("focus_mode.xml"), //NON-NLS
  category = SettingsCategory.UI
)
public final class MTConfig implements PersistentStateComponent<MTConfig>,
  MTBaseConfig<MTForm, MTConfig>, Cloneable {
  //region ----------------- [Defaults] ------------------
  public static final String FOCUS_COLOR = MTAccents.CARBON.getHexColor();
  //endregion [Defaults]

  // region ---------------- [Properties] -----------------

  @Property
  boolean isFocusMode = false;

  @Property
  boolean overrideFocusColor = true;

  @Property
  String focusColor = FOCUS_COLOR;

  @Transient
  private transient boolean isReset = false;

  //endregion [Properties]

  boolean firstRun = true;

  /**
   * Get instance of the config from the ServiceManager
   *
   * @return the MTConfig instance
   */
  public static MTConfig getInstance() {
    return ApplicationManager.getApplication().getService(MTConfig.class);
  }

  /**
   * Clone the current instance
   *
   * @return Object
   */
  @SuppressWarnings("MethodDoesntCallSuperMethod")
  @Override
  public MTConfig clone() {
    return XmlSerializerUtil.createCopy(this);
  }

  /**
   * Get the state of MTConfig
   */
  @NotNull
  @Override
  public MTConfig getState() {
    return this;
  }

  /**
   * Load the state from XML
   *
   * @param state the MTConfig instance
   */
  @Override
  public void loadState(@NotNull final MTConfig state) {
    final boolean changed = state != this;
    XmlSerializerUtil.copyBean(state, this);

    if (changed && !firstRun) {
      ApplicationManager.getApplication().invokeAndWait(this::fireChanged);
    }
    firstRun = false;
  }

  /**
   * Fire event before saving the form values
   */
  @Override
  public void fireBeforeChanged(final MTForm form) {
    ApplicationManager.getApplication().getMessageBus()
      .syncPublisher(MTTopics.CONFIG)
      .beforeConfigChanged(this, form);
  }

  /**
   * Fire an event to the application bus that the settings have changed
   */
  @Override
  public void fireChanged() {
    ApplicationManager.getApplication().getMessageBus()
      .syncPublisher(MTTopics.CONFIG)
      .configChanged(this);
  }

  /**
   * Apply settings according to the form
   *
   * @param form form to read
   */
  @Override
  @SuppressWarnings({"CallToSimpleSetterFromWithinClass"
  })
  public void applySettings(final MTForm form) {
    // First fire before change
    fireBeforeChanged(form);
    isReset = false;

    setFocusModeEnabled(form.isFocusModeEnabled());
    setOverrideFocusColor(form.isOverrideFocusColor());
    setFocusColor(ColorUtil.toHex(form.getFocusModeColor()));

    // Then fire changed
    fireChanged();
  }

  /**
   * Reset the settings to the default values
   */
  @Override
  public void resetSettings() {
    focusColor = FOCUS_COLOR;
    isFocusMode = false;
    overrideFocusColor = true;
  }


  //region ----------- [Focus Mode] -----------
  public boolean isFocusModeEnabled() {
    return isFocusMode;
  }

  public void setFocusModeEnabled(final boolean focusMode) {
    isFocusMode = focusMode;
  }

  public boolean isFocusModeEnabledChanged(final boolean focusMode) {
    return isFocusMode != focusMode;
  }

  public void setOverrideFocusColor(final boolean overrideFocusColor) {
    this.overrideFocusColor = overrideFocusColor;
  }

  public boolean isOverrideFocusColorChanged(final boolean overrideFocus) {
    return overrideFocusColor != overrideFocus;
  }

  public boolean isOverrideFocusColor() {
    return overrideFocusColor;
  }

  public void setFocusColor(final String focusColor) {
    this.focusColor = focusColor;
  }

  public boolean isFocusColorChanged(final Color focusColor) {
    return !Objects.equals(this.focusColor, ColorUtil.toHex(focusColor));
  }

  public String getFocusColor() {
    return focusColor;
  }

  @NotNull
  public String getThemeFocusColor() {
    if (overrideFocusColor) {
      return ColorUtil.toHex(UIUtil.getControlColor());
    }
    return focusColor;
  }

  //endregion [Focus Mode]

  //endregion [Other Settings]


  //region ~~~~~~~~~ [Other info] ~~~~~~~~~~~
  public boolean isReset() {
    return isReset;
  }

  //endregion [Other info]

}
