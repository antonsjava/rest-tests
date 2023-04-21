/*
 *
 */
package sk.antons.resttests.condition;

import sk.antons.resttests.http.HttpResponse;

/**
 * Generic class for classes which resolve selectors to text from response.
 * @author antons
 */
public interface TextResolver {
    String resolve(HttpResponse response, String selector);
}
