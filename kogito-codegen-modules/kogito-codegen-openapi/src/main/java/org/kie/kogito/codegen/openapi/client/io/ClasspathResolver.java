/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.openapi.client.io;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.openapi.client.OpenApiSpecDescriptor;
import org.kie.kogito.codegen.openapi.client.OpenApiUtils;

import static java.util.Objects.requireNonNull;

/**
 * Resolves the schema "classpath:" in a given OpenApi operation definition.
 * For example: "classpath://specs/swagger.json"
 */
public class ClasspathResolver extends AbstractPathResolver {

    private static final String CLASSPATH_SEP = ".jar!/";

    protected ClasspathResolver(final KogitoBuildContext context) {
        super(context);
    }

    @Override
    public String resolve(OpenApiSpecDescriptor descriptor) {
        OpenApiUtils.requireValidSpecURI(descriptor);
        String resourceUri = descriptor.getURI().getPath();
        System.out.println("ClasspathResolver.resolve, descriptor.getURI: " + descriptor.getURI().toString() + ", resourceUri: " + resourceUri);
        if (PathResolverFactory.CLASSPATH.equals(descriptor.getURI().getScheme())) {
            resourceUri = descriptor.getURI().getHost() + resourceUri;
        }
        String classpathPath = requireNonNull(this.context.getClassLoader().getResource(resourceUri), "Resource URI can't be found. Descriptor: " + descriptor).getPath();
        System.out.println("ClasspathResolver.classpathPath inicial: " + classpathPath);

        // OpenApi generator tool doesn't have access to the application build classpath, so we save to a temp location (/target) where it can be accessed
        if (classpathPath.contains(CLASSPATH_SEP)) {
            System.out.println("ClasspathResolver tenemos el class separator");
            classpathPath = this.saveFileToTempLocation(descriptor, this.context.getClassLoader().getResourceAsStream(resourceUri));
            System.out.println("ClasspathResolver nos vamos con: " + classpathPath);

        } else {
            try {
                System.out.println("ClasspathResolver NO tenemos el class separator, tenemos q convertir el path en seco");
                classpathPath = Paths.get(requireNonNull(this.context.getClassLoader().getResource(resourceUri), "Resource URI can't be found. Descriptor: " + descriptor).toURI()).toString();
            } catch (URISyntaxException e) {
                System.out.println("ClasspathResolver resolviendo Path en el class path ha petado" + e.getMessage());
                e.printStackTrace();
                throw new IllegalArgumentException("Classpath resource was resolved to an invalid URI for the Descriptor: " + descriptor, e);
            }

            System.out.println("ClasspathResolver.classpathPath final: " + classpathPath);
        }

        return classpathPath;
    }
}
