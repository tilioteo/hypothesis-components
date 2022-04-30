package org.hypothesis.components.data;

import com.vaadin.event.ShortcutAction;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author kamil
 */
public class ShortcutConstants {

	public static final Map<String, Integer> SHORTCUT_MAP = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER) {
		{
			put("A", ShortcutAction.KeyCode.A);
			put("B", ShortcutAction.KeyCode.B);
			put("C", ShortcutAction.KeyCode.C);
			put("D", ShortcutAction.KeyCode.D);
			put("E", ShortcutAction.KeyCode.E);
			put("F", ShortcutAction.KeyCode.F);
			put("G", ShortcutAction.KeyCode.G);
			put("H", ShortcutAction.KeyCode.H);
			put("I", ShortcutAction.KeyCode.I);
			put("J", ShortcutAction.KeyCode.J);
			put("K", ShortcutAction.KeyCode.K);
			put("L", ShortcutAction.KeyCode.L);
			put("M", ShortcutAction.KeyCode.M);
			put("N", ShortcutAction.KeyCode.N);
			put("O", ShortcutAction.KeyCode.O);
			put("P", ShortcutAction.KeyCode.P);
			put("Q", ShortcutAction.KeyCode.Q);
			put("R", ShortcutAction.KeyCode.R);
			put("S", ShortcutAction.KeyCode.S);
			put("T", ShortcutAction.KeyCode.T);
			put("U", ShortcutAction.KeyCode.U);
			put("V", ShortcutAction.KeyCode.V);
			put("W", ShortcutAction.KeyCode.W);
			put("X", ShortcutAction.KeyCode.X);
			put("Y", ShortcutAction.KeyCode.Y);
			put("Z", ShortcutAction.KeyCode.Z);

			put("Num0", ShortcutAction.KeyCode.NUM0);
			put("Num1", ShortcutAction.KeyCode.NUM1);
			put("Num2", ShortcutAction.KeyCode.NUM2);
			put("Num3", ShortcutAction.KeyCode.NUM3);
			put("Num4", ShortcutAction.KeyCode.NUM4);
			put("Num5", ShortcutAction.KeyCode.NUM5);
			put("Num6", ShortcutAction.KeyCode.NUM6);
			put("Num7", ShortcutAction.KeyCode.NUM7);
			put("Num8", ShortcutAction.KeyCode.NUM8);
			put("Num9", ShortcutAction.KeyCode.NUM9);

			put("F1", ShortcutAction.KeyCode.F1);
			put("F2", ShortcutAction.KeyCode.F2);
			put("F3", ShortcutAction.KeyCode.F3);
			put("F4", ShortcutAction.KeyCode.F4);
			put("F5", ShortcutAction.KeyCode.F5);
			put("F6", ShortcutAction.KeyCode.F6);
			put("F7", ShortcutAction.KeyCode.F7);
			put("F8", ShortcutAction.KeyCode.F8);
			put("F9", ShortcutAction.KeyCode.F9);
			put("F10", ShortcutAction.KeyCode.F10);
			put("F11", ShortcutAction.KeyCode.F11);
			put("F12", ShortcutAction.KeyCode.F12);

			put("Spacebar", ShortcutAction.KeyCode.SPACEBAR);
			put("Enter", ShortcutAction.KeyCode.ENTER);
			put("Esc", ShortcutAction.KeyCode.ESCAPE);
			put("PgUp", ShortcutAction.KeyCode.PAGE_UP);
			put("PgDn", ShortcutAction.KeyCode.PAGE_DOWN);
			put("Tab", ShortcutAction.KeyCode.TAB);
			put("Left", ShortcutAction.KeyCode.ARROW_LEFT);
			put("Right", ShortcutAction.KeyCode.ARROW_RIGHT);
			put("Up", ShortcutAction.KeyCode.ARROW_UP);
			put("Down", ShortcutAction.KeyCode.ARROW_DOWN);
			put("Backspace", ShortcutAction.KeyCode.BACKSPACE);
			put("Del", ShortcutAction.KeyCode.DELETE);
			put("Ins", ShortcutAction.KeyCode.INSERT);
			put("End", ShortcutAction.KeyCode.END);
			put("Home", ShortcutAction.KeyCode.HOME);
		}
	};

	public static final Map<String, Integer> MODIFIER_MAP = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER) {
		{
			put("Shift", ShortcutAction.ModifierKey.SHIFT);
			put("Ctrl", ShortcutAction.ModifierKey.CTRL);
			put("Alt", ShortcutAction.ModifierKey.ALT);
			put("Meta", ShortcutAction.ModifierKey.META);
		}
	};

}
