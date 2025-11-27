/**
 * ========================================
 * EMPTYORDEREXCEPTION.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbraton
 * ========================================
 */

package Examns;

public class EmptyOrderException extends Exception {
	private static final long serialVersionUID = 1L;

	public EmptyOrderException(String message) {
		super(message);
	}
}
