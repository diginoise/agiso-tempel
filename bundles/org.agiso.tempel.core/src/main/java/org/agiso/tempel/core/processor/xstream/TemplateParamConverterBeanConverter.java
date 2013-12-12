/* org.agiso.tempel.core.processor.xstream.converter.TemplateParamConverterBeanConverter (12-12-2013)
 * 
 * TemplateParamConverterBeanConverter.java
 * 
 * Copyright 2013 agiso.org
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
package org.agiso.tempel.core.processor.xstream;

import org.agiso.tempel.api.ITemplateParamConverter;
import org.agiso.tempel.core.model.beans.TemplateParamConverterBean;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Konwerter XStream obsługujący konwersję znacznika &lt;converter&gt; definicji
 * konwertera parametru szablonu.
 * 
 * @author <a href="mailto:kkopacz@agiso.org">Karol Kopacz</a>
 */
class TemplateParamConverterBeanConverter extends AbstractConfigurableConverter {
	public TemplateParamConverterBeanConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
		super(mapper, reflectionProvider);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return type.equals(TemplateParamConverterBean.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Class<?> converterClass = null;
		String converterClassName = reader.getAttribute("class");
		if(converterClassName == null) {
			throw new ConversionException("Converter 'class' not defined");
		}
		if(converterClassName.isEmpty()) {
			throw new ConversionException("Empty string is invalid 'class' value");
		}
		try {
			converterClass = Class.forName(converterClassName);
			if(!ITemplateParamConverter.class.isAssignableFrom(converterClass)) {
				throw new ConversionException("Invalid converter 'class' value");
			}
		} catch(ClassNotFoundException e) {
			throw new ConversionException("Unknown converter 'class'", e);
		}

		TemplateParamConverterBean templateParamConverter = new TemplateParamConverterBean();
		templateParamConverter.setConverterClass((Class<ITemplateParamConverter<?>>)converterClass);
		templateParamConverter.setProperties(readProperties(reader));
		return templateParamConverter;
	}
}
