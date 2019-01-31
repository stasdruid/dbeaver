/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2019 Serge Rider (serge@jkiss.org)
 * Copyright (C) 2011-2012 Eugene Fradkin (eugene.fradkin@gmail.com)
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
package org.jkiss.dbeaver.ext.mysql.edit;

import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.mysql.model.MySQLCatalog;
import org.jkiss.dbeaver.ext.mysql.model.MySQLEvent;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.edit.DBEPersistAction;
import org.jkiss.dbeaver.model.impl.DBSObjectCache;
import org.jkiss.dbeaver.model.impl.edit.SQLDatabasePersistAction;
import org.jkiss.dbeaver.model.impl.sql.edit.SQLObjectEditor;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.ui.UITask;
import org.jkiss.dbeaver.ui.editors.object.struct.CreateProcedurePage;
import org.jkiss.utils.CommonUtils;

import java.util.List;
import java.util.Map;

/**
 * MySQLProcedureManager
 */
public class MySQLEventManager extends SQLObjectEditor<MySQLEvent, MySQLCatalog> {

	/*@Nullable
    @Override
    public DBSObjectCache<MySQLCatalog, MySQLEvent> getObjectsCache(MySQLEvent object)
    {
        return object.getContainer().getProceduresCache();
    }*/

    @Override
    public long getMakerOptions(DBPDataSource dataSource)
    {
        return FEATURE_EDITOR_ON_CREATE;
    }

    @Override
    protected void validateObjectProperties(ObjectChangeCommand command, Map<String, Object> options)
        throws DBException
    {
        if (CommonUtils.isEmpty(command.getObject().getName())) {
            throw new DBException("Event name cannot be empty");
        }
        /*if (CommonUtils.isEmpty(command.getObject().getDeclaration())) {
            throw new DBException("Procedure body cannot be empty");
        }*/
    }

   /* @Override
    protected MySQLEvent createDatabaseObject(DBRProgressMonitor monitor, DBECommandContext context, final MySQLCatalog parent, Object copyFrom)
    {
        return new UITask<MySQLEvent>() {
            @Override
            protected MySQLEvent runTask() {
                CreateEventPage editPage = new CreateEventPage(parent);
                if (!editPage.edit()) {
                    return null;
                }
                MySQLEvent newEvent = new MySQLEvent(parent);
                newEvent.setProcedureType(editPage.getProcedureType());
                newEvent.setName(editPage.getProcedureName());
                return newEvent;
            }
        }.execute();
    }*/

    @Override
    protected void addObjectCreateActions(DBRProgressMonitor monitor, List<DBEPersistAction> actions, ObjectCreateCommand command, Map<String, Object> options)
    {
        createOrReplaceEventQuery(actions, command.getObject());
    }

    @Override
    protected void addObjectModifyActions(DBRProgressMonitor monitor, List<DBEPersistAction> actionList, ObjectChangeCommand command, Map<String, Object> options)
    {
        createOrReplaceEventQuery(actionList, command.getObject());
    }

    @Override
    protected void addObjectDeleteActions(List<DBEPersistAction> actions, ObjectDeleteCommand command, Map<String, Object> options)
    {
        actions.add(
            new SQLDatabasePersistAction("Drop event", "DROP " + command.getObject().getEventType() + " " + command.getObject().getName()) //$NON-NLS-2$
        );
    }

    private void createOrReplaceEventQuery(List<DBEPersistAction> actions, MySQLEvent event)
    {
        actions.add(
            new SQLDatabasePersistAction("Drop event", "DROP " + event.getEventType() + " IF EXISTS " + event.getName())); //$NON-NLS-2$ //$NON-NLS-3$
        actions.add(
            new SQLDatabasePersistAction("Create event", event.getObjectDefinitionText())); //DBPScriptObject.EMPTY_OPTIONS
    }

}

