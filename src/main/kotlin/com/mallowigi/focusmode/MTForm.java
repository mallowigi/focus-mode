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

import com.intellij.openapi.Disposable;
import com.intellij.ui.ColorPanel;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.components.OnOffButton;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.mallowigi.focusmode.config.MTBaseConfig;
import com.mallowigi.focusmode.messages.FocusModeBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

@SuppressWarnings({
  "InstanceVariableMayNotBeInitialized",
  "FeatureEnvy",
  "rawtypes",
  "unused",
  "PublicMethodNotExposedInInterface"
})
public class MTForm implements MTFormUI, Disposable {
  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  // Generated using JFormDesigner non-commercial license
  private JPanel content;
  private JCheckBox focusModeCheckbox;
  private JLabel overrideFocusColorLabel;
  private OnOffButton overrideFocusModeSwitch;
  private ColorPanel focusModeColorChooser;
  // GEN-END:variables

  @Override
  public final void init() {
    initComponents();
    setupComponents();
  }

  @Override
  public final JComponent getContent() {
    return content;
  }

  private void afterStateSet() {
  }

  @Override
  public void dispose() {
    // Nothing to dispose just yet
  }

  public final void setFormState(final MTBaseConfig config) {
    final MTConfig mtConfig = (MTConfig) config;

    setFocusModeEnabled(mtConfig.isFocusModeEnabled());
    setIsOverrideFocusColor(mtConfig.isOverrideFocusColor());
    setFocusModeColor(ColorUtil.fromHex(mtConfig.getFocusColor()));

    afterStateSet();
  }

  @Override
  public final void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    // Generated using JFormDesigner non-commercial license
    final ResourceBundle bundle = FocusModeBundle.getBundle();
    final DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
    content = new JPanel();
    focusModeCheckbox = new JCheckBox();
    overrideFocusColorLabel = new JLabel();
    overrideFocusModeSwitch = new OnOffButton();
    focusModeColorChooser = new ColorPanel();

    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  public final boolean isModified(final MTBaseConfig config) {
    final MTConfig mtConfig = (MTConfig) config;

    boolean modified = mtConfig.isReset();
    modified = modified || mtConfig.isFocusModeEnabledChanged(isFocusModeEnabled());
    modified = modified || mtConfig.isOverrideFocusColorChanged(isOverrideFocusColor());
    modified = modified || mtConfig.isFocusColorChanged(getFocusModeColor());

    return modified;
  }


  //region [Focus Mode]
  public final boolean isFocusModeEnabled() {
    return focusModeCheckbox.isSelected();
  }

  private void setFocusModeEnabled(final boolean enabled) {
    focusModeCheckbox.setSelected(enabled);
    enableDisableFocusMode(enabled);
  }

  public final boolean isOverrideFocusColor() {
    return overrideFocusModeSwitch.isSelected();
  }

  private void setIsOverrideFocusColor(final boolean enabled) {
    overrideFocusModeSwitch.setSelected(enabled);
  }

  public final Color getFocusModeColor() {
    return focusModeColorChooser.getSelectedColor();
  }

  private void setFocusModeColor(final Color color) {
    focusModeColorChooser.setSelectedColor(color);
  }

  // endregion [Focus Mode]


  //region ~~~~~~~~~~~~ [Enabled listeners] ~~~~~~~~~~~~~~~~~~


  private void enableDisableFocusMode(final boolean isFocusModeColor) {
    overrideFocusModeSwitch.setEnabled(isFocusModeColor);
    overrideFocusColorLabel.setEnabled(isFocusModeColor);
    focusModeColorChooser.setEnabled(isFocusModeColor && !overrideFocusModeSwitch.isSelected());
  }

  private void overrideFocusModeSwitchActionPerformed(final ActionEvent e) {
    focusModeColorChooser.setEnabled(focusModeCheckbox.isSelected() && !overrideFocusModeSwitch.isSelected());
  }

  //endregion [Enabled listeners]

  //region ~~~~~~~~~~~~ [Events - Actions Listeners] ~~~~~~~~~~~~


  private void focusModeCheckboxActionPerformed(final ActionEvent e) {
    enableDisableFocusMode(isFocusModeEnabled());
  }

  //endregion [Events - Actions Listeners]

  @Override
  public final void setupComponents() {
    // Disable features that are not available on certain platforms or versions
    disableFeatures();
  }


  /**
   * Disable features that are not available on certain platforms/versions/etc
   */
  private static void disableFeatures() {
    // to be implemented if necessary
  }

}
