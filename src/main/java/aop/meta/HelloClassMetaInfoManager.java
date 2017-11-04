package aop.meta;

/*-
 * #%L
 * ACT Framework
 * %%
 * Copyright (C) 2014 - 2017 ActFramework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static act.Destroyable.Util.destroyAll;

import act.asm.Type;
import act.plugin.finder.ScannerFinder;
import act.util.DestroyableBase;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;
import org.osgl.util.C;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloClassMetaInfoManager extends DestroyableBase {
    protected static Logger logger = LogManager.get(HelloClassMetaInfoManager.class);
    private Map<String, HelloClassMetaInfo> metaInfoMap = C.newMap();

    public HelloClassMetaInfoManager() {
    }

    @Override
    protected void releaseResources() {
        destroyAll(metaInfoMap.values(), ApplicationScoped.class);
        metaInfoMap.clear();
        super.releaseResources();
    }

    public void register(HelloClassMetaInfo metaInfo) {
        String className = Type.getObjectType(metaInfo.className()).getClassName();
        metaInfoMap.put(className, metaInfo);
        logger.debug("Hello meta class info registered for: %s", className);
    }

    public HelloClassMetaInfo getMetaInfo(String className) {
        return metaInfoMap.get(className);
    }

}
