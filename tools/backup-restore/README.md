# Build

        mvn package -pl tools/backup-restore -am -DskipTests

# Backup

        java -jar tools/backup-restore/target/visallo-backup-restore-*-with-dependencies.jar \
          --accumuloInstanceName=visallo \
          --accumuloUsername=root \
          --accumuloPassword=password \
          --zookeeperServers=visallo-dev \
          --hadoopFsDefaultFS=visallo-dev:8020 \
          --hadoopDfsClientUseDatanodeHostname \
          --hadoopUsername=root \
          backup

# Restore

**WARNING:**
Restore is a destructive process and will move the tablet files from the restore directory (unless you use the `--hdfsRestoreTempDirectory=` option)
See _org.apache.accumulo.core.client.admin.TableOperations.importTable_

        java -jar tools/backup-restore/target/visallo-backup-restore-*-with-dependencies.jar \
          --accumuloInstanceName=visallo \
          --accumuloUsername=root \
          --accumuloPassword=password \
          --zookeeperServers=visallo-dev \
          --hadoopFsDefaultFS=visallo-dev:8020 \
          --hadoopDfsClientUseDatanodeHostname \
          --hadoopUsername=root \
          --hdfsRestoreDirectory=/backup/20150121T1633 \
          --hdfsRestoreTempDirectory=/tmp/restore-20150121T1633 \
          restore
