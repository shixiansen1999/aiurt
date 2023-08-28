/* Licensed under the Apache License, Version 2.0 (the "License");
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
 */
package com.aiurt.modules.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;


/**
 * @author fgw
 * @desc flowable6.7.2 model数据配置，flowable6.7.2 不再使用act_re_model 而是使用act_de_model
 */
@Configuration
@ComponentScan(value = {
        "org.flowable.ui.modeler.repository",
        "org.flowable.ui.modeler.service",
        "org.flowable.ui.common.tenant",
        "org.flowable.ui.common.repository"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {RemoteIdmService.class})
})
public class DatabaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";

    /**
     * Liquibase 是一个用于数据库版本控制和迁移的开源工具。它允许开发团队对数据库模式进行版本管理，使得数据库的结构和数据可以随着应用程序的演变而同步变化。
     * @param dataSource
     * @return
     */
    @Bean
    public Liquibase liquibase(DataSource dataSource) {
        LOGGER.info("Configuring Liquibase");

        Liquibase liquibase = null;
        try {
            // 创建连接
            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
            // database它是 Liquibase 的核心对象，用于管理数据库变更，获取 Liquibase 的数据库工厂实例，
            // 然后 findCorrectDatabaseImplementation(connection) 用于识别数据库的实际实现。这可以根据数据库连接自动检测要使用的数据库类型
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            // 修改了 Liquibase 数据库变更日志表的表名。它将表名更改为一个新的表名，该表名由 LIQUIBASE_CHANGELOG_PREFIX 和原始表名组成
            database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
            database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());

            liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("flowable");
            return liquibase;
        } catch (Exception e) {
            throw new InternalServerErrorException("Error creating liquibase database", e);
        } finally {
            closeDatabase(liquibase);
        }
    }

    private void closeDatabase(Liquibase liquibase) {
        if (liquibase != null) {
            Database database = liquibase.getDatabase();
            if (database != null) {
                try {
                    database.close();
                } catch (DatabaseException e) {
                    LOGGER.warn("Error closing database", e);
                }
            }
        }
    }

    @Bean(destroyMethod = "clearCache")
    @Qualifier("flowableModeler")
    public SqlSessionTemplate modelerSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
