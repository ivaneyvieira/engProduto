import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/styles/gridTotal.css?inline';

injectGlobalCss($cssFromFile_0.toString(), 'CSSImport end', document);
import $cssFromFile_1 from 'Frontend/generated/jar-resources/styles/form-layout-number-field-styles.css?inline';

injectGlobalCss($cssFromFile_1.toString(), 'CSSImport end', document);
import $cssFromFile_2 from 'Frontend/generated/jar-resources/styles/label-positions.css?inline';
const $css_2 = typeof $cssFromFile_2  === 'string' ? unsafeCSS($cssFromFile_2) : $cssFromFile_2;
registerStyles('super-text-field', $css_2, {moduleId: 'flow_css_mod_2'});
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/login/theme/lumo/vaadin-login-form.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '161d1f7209b0261ba05fa8a79793d7483493cfa1b8fed9aedde9b377ff7f01a6') {
    pending.push(import('./chunks/chunk-49d6e85c716e2152bc925736d73e3426d9f377d56d3167a782a5fd5bd77d08cd.js'));
  }
  if (key === '58baff4c833082b9ad8f171d381b619bcac5c150a43d495206faed9084c8e1bf') {
    pending.push(import('./chunks/chunk-94320970b3797ff1f9d7d7b9388b704165f18b0d7b2b142f8cb3060525951476.js'));
  }
  if (key === 'f69f71500af10cc96b9c68e8f0a4a699c9e6c32244057d731c0f02a1094b2149') {
    pending.push(import('./chunks/chunk-49d6e85c716e2152bc925736d73e3426d9f377d56d3167a782a5fd5bd77d08cd.js'));
  }
  if (key === 'c49148e16ee4ebd328c610dc6427b4f5f7938fcfe35f44e0bbd4f3377553412b') {
    pending.push(import('./chunks/chunk-70de408bde3d8c95be44d35ddfac01918e7ec1c20261f50d7a01633d2a575686.js'));
  }
  if (key === '3467c1e57258ce79e9b0ebb703f0a87ba7417ad78be532c548a8ec0f54a52f45') {
    pending.push(import('./chunks/chunk-70de408bde3d8c95be44d35ddfac01918e7ec1c20261f50d7a01633d2a575686.js'));
  }
  if (key === '7e2291a8a54b332c2e68ba7ae207164c4eada80218468259fb5b5b68504814c3') {
    pending.push(import('./chunks/chunk-49d6e85c716e2152bc925736d73e3426d9f377d56d3167a782a5fd5bd77d08cd.js'));
  }
  if (key === '0f46805a1127bbc5be5d3d730a08cf089981648e1a86df78b87330288d529e05') {
    pending.push(import('./chunks/chunk-49d6e85c716e2152bc925736d73e3426d9f377d56d3167a782a5fd5bd77d08cd.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;