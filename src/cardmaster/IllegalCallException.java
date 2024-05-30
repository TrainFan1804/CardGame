package cardmaster;

import cardmaster.Game.Mode;

/**
 * Eine IllegalCallException wird geworfen, wenn eine Methode in {@link Game}
 * aufgerufen wird, die nicht zu in dem aktuellen {@link Game.Mode} aufgerufen
 * werden darf
 */
public class IllegalCallException extends RuntimeException {

        /**
         * Wirft eine IllegalCallException
         * 
         * @param methodName    Der Name der Methode, in der die Exception geschmissen wird
         * @param currentMode   Der aktuelle Modus in dem sich das Spiel befindet
         * @param expMode       Der Modus der von der aufgerufenen Methode erwartet wird
         */
        public IllegalCallException(String methodName, Mode currentMode, Mode expMode) {

                super("Error in " + methodName
                        + ". Current Mode" + currentMode
                        + ". Expected mode: " + expMode + ".");
        }
}
