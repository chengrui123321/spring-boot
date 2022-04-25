/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.env;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * Strategy to load '.properties' files into a {@link PropertySource}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Madhura Bhave
 * @since 1.0.0
 *
 * 加载配置信息到 {@link PropertySource}
 */
public class PropertiesPropertySourceLoader implements PropertySourceLoader {

	/**
	 * xml 文件扩展名
	 */
	private static final String XML_FILE_EXTENSION = ".xml";

	/**
	 * 获取配置文件扩展名，默认 .properties/.xml
	 * @return
	 */
	@Override
	public String[] getFileExtensions() {
		return new String[] { "properties", "xml" };
	}

	/**
	 * 加载 properties 文件
	 * @param name the root name of the property source. If multiple documents are loaded
	 * an additional suffix should be added to the name for each source loaded.
	 * @param resource the resource to load
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
		// 加载配置信息
		Map<String, ?> properties = loadProperties(resource);
		if (properties.isEmpty()) {
			return Collections.emptyList();
		}
		return Collections
				.singletonList(new OriginTrackedMapPropertySource(name, Collections.unmodifiableMap(properties), true));
	}

	/**
	 * 加载配置
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, ?> loadProperties(Resource resource) throws IOException {
		// 获取文件名称
		String filename = resource.getFilename();
		// 如果名称不为空，并且文件以 .xml 后缀
		if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
			// 加载配置信息到 Properties 中
			return (Map) PropertiesLoaderUtils.loadProperties(resource);
		}
		return new OriginTrackedPropertiesLoader(resource).load();
	}

}
