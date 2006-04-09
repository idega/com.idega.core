package com.idega.data.query.output;

import com.idega.data.query.Flag;

/**
 * The Output is where the elements of the query output their bits of SQL to.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public class Output implements Flag{

    protected boolean flag = false;
    /**
     * @param indent String to be used for indenting (e.g. "", "  ", "       ", "\t")
     */
    public Output(String indent) {
    }

    private StringBuffer result = new StringBuffer();
    private boolean newLineComing;

    public String toString() {
        return this.result.toString();
    }
    
    public Output print(Outputable o){
        writeNewLineIfNeeded();
        Output out = new Output("");
        out.flag(this.isFlagged());
        o.write(out);
        this.result.append(out.toString());
        return this;
    }

    
    public Output print(Object o) {
        writeNewLineIfNeeded();
        this.result.append(o);
        return this;
    }

    public Output print(char c) {
        writeNewLineIfNeeded();
        this.result.append(c);
        return this;
    }

    public Output println(Object o) {
        writeNewLineIfNeeded();
        this.result.append(o);
        this.newLineComing = true;
        return this;
    }

    public Output println() {
        this.newLineComing = true;
        return this;
    }

    public void indent() {
//        currentIndent.append(indent);
    }

    public void unindent() {
//        currentIndent.setLength(currentIndent.length() - indent.length());
    }

    private void writeNewLineIfNeeded() {
        if (this.newLineComing) {
            //result.append('\n').append(currentIndent);
        	   this.result.append(' ');//.append(currentIndent);
            this.newLineComing = false;
        }
    }
    
    public boolean isFlagged(){
        return this.flag;
    }
    public void flag(boolean flag){
        this.flag = flag;
    }
}
