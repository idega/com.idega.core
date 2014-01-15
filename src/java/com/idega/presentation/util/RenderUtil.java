package com.idega.presentation.util;

import java.io.Writer;

public interface RenderUtil {

	public void doRemoveNeedlessContentAndSetRealPageTitle(Writer writer, String newTitle, String oldTitle);

}