Function InsertElementUnderInsertionPoint(o,html)
	Select Case o.DOM.selection.Type
		Case "None", "Text"
			Set rg = o.DOM.selection.createRange
			rg.collapse
			rg.parentElement.outerHTML = html
		Case "Control"
			Set ctlRg = o.DOM.selection.createRange
			ctlRg.commonParentElement.outerHTML = html
	End Select
End Function

Function GetCmdValue(o,cmd)
	Dim foo
	foo = o.ExecCommand(cmd)
	GetCmdValue = foo
End Function

Function vbs_getControlElement(ctlRg)
	Set vbs_getControlElement = ctlRg.commonParentElement
End Function