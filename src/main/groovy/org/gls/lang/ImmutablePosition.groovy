package org.gls.lang

import groovy.transform.TypeChecked
import org.eclipse.lsp4j.Position

/**
 * Created by johan on 4/8/18.
 */
@TypeChecked
class ImmutablePosition extends Position {

    public ImmutablePosition(final int line, final int character) {
        super(line, character)
    }

    @Override
    void setCharacter(int character) {
    }

    @Override
    void setLine(int line) {
    }
}
