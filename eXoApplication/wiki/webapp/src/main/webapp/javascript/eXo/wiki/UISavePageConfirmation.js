/**
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

function UISavePageConfirmation() {
};

/* ie bug you cannot have more than one button tag */
/**
 * Submits a form with the given action and the given parameters
 */

UISavePageConfirmation.prototype.validateSave = function(pageTitleinputId, currentMode) {
  var confirmMask = document.getElementById("ConfirmMask");
  var pageTitleInput = document.getElementById(pageTitleinputId);
  var confirmMessage = eXo.core.DOMUtil.findFirstDescendantByClass(confirmMask, "div", "ConfirmMessage");
  if ((currentMode == "NEW") && (pageTitleInput.value == "Untitled")) {
    confirmMask.style.display = "block";
    confirmMessage.innerHTML = "You are about to save an Untitled page.";
    return false;
  } else if (currentMode == "EDIT") {
    confirmMask.style.display = "block";
    confirmMessage.innerHTML = "Your changes will be saved to history.<br/>Are you sure you want to apply this changes?";
    return false;
  }
  return true;
};

UISavePageConfirmation.prototype.closeConfirm = function() {
  var confirmMask = document.getElementById("ConfirmMask");
  confirmMask.style.display = "none";
  return false;
}
UISavePageConfirmation.prototype.initDragDrop = function() {
  var confirmMask = document.getElementById("ConfirmMask");
  var confirmBox = eXo.core.DOMUtil.findFirstDescendantByClass(confirmMask, "div", "ConfirmBox");
  var confirmTitle = eXo.core.DOMUtil.findFirstDescendantByClass(confirmMask, "div", "ConfirmTitle");
  confirmTitle.onmousedown = function(e) {
    eXo.core.DragDrop.init(null, confirmTitle, confirmBox, e);
  }
}
eXo.wiki.UISavePageConfirmation = new UISavePageConfirmation();