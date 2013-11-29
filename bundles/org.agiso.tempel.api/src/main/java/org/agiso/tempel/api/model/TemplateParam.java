/* org.agiso.tempel.api.model.TemplateParam (15-09-2012)
 * 
 * TemplateParam.java
 * 
 * Copyright 2012 agiso.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agiso.tempel.api.model;

/**
 * 
 * 
 * @author <a href="mailto:kkopacz@agiso.org">Karol Kopacz</a>
 */
public interface TemplateParam<C extends TemplateParamConverter, V extends TemplateParamValidator> extends Cloneable {
	/**
	 * @return Identyfikator (klucz) parametru.
	 */
	public String getKey();

	/**
	 * @return Nazwa parametru.
	 */
	public String getName();

	/**
	 * @return Pełna nazwa klasy typu parametru. Domyślnie {@link String}.
	 */
	public String getType();

	/**
	 * @return Konwerter parametru.
	 */
	public C getConverter();
	public void setConverter(C converter);

	/**
	 * @return Walidator parametru.
	 */
	public V getValidator();
	public void setValidator(V validator);

	/**
	 * Zwraca informację, czy parametr jest oznaczony, tzn. czy jego wartość
	 * domyślna jest ostateczna. Parametry nieoznaczone nie muszą mieć wartości
	 * domyślnych i wypełniane są ręcznie przez użytkownika w konsoli programu.
	 * Parametry oznaczone muszą mieć wartość domyślną określoną w definicji
	 * szablonu i nie są bezpośrednio modyfikowalne przez użytkownika.
	 * 
	 * @return Cecha 'Oznaczony' parametru.
	 */
	public Boolean getFixed();
	public void setFixed(Boolean fixed);

	/**
	 * @return Wartość domyślna parametru.
	 */
	public String getValue();
	public void setValue(String value);

//	--------------------------------------------------------------------------
	public TemplateParam<C, V> clone();
}
