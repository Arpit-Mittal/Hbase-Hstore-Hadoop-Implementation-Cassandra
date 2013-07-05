/* This file is part of VoltDB.
 * Copyright (C) 2008-2010 VoltDB L.L.C.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.voltdb;

import java.util.ArrayList;
import java.util.Hashtable;

import org.voltdb.VoltDB.Configuration;
import org.voltdb.catalog.Catalog;
import org.voltdb.catalog.Cluster;
import org.voltdb.catalog.Column;
import org.voltdb.catalog.Database;
import org.voltdb.catalog.Host;
import org.voltdb.catalog.Partition;
import org.voltdb.catalog.Procedure;
import org.voltdb.catalog.Site;
import org.voltdb.catalog.Table;
import org.voltdb.fault.FaultDistributorInterface;
import org.voltdb.messaging.HostMessenger;
import org.voltdb.messaging.Messenger;
import org.voltdb.network.VoltNetwork;

import edu.brown.catalog.CatalogUtil;
import edu.brown.hstore.PartitionExecutor;

public class MockVoltDB implements VoltDBInterface
{
    private final Catalog m_catalog;
    private CatalogContext m_context;
    final String m_clusterName = "cluster";
    final String m_databaseName = "database";
    StatsAgent m_statsAgent = null;
    int m_howManyCrashes = 0;
    FaultDistributorInterface m_faultDistributor = null;
    HostMessenger m_hostMessenger = null;

    public MockVoltDB()
    {
        m_catalog = new Catalog();
        m_catalog.execute("add / clusters " + m_clusterName);
        m_catalog.execute("add " + m_catalog.getClusters().get(m_clusterName).getPath() + " databases " +
                          m_databaseName);
        Cluster cluster = m_catalog.getClusters().get(m_clusterName);
        assert(cluster != null);

        /*Host host = cluster.getHosts().add("0");
        Site execSite = cluster.getSites().add("1");
        Site initSite = cluster.getSites().add("0");
        Partition partition = cluster.getPartitions().add("0");

        host.setIpaddr("localhost");

        initSite.setHost(host);
        initSite.setIsexec(false);
        initSite.setInitiatorid(0);
        initSite.setIsup(true);

        execSite.setHost(host);
        execSite.setIsexec(true);
        execSite.setIsup(true);
        execSite.setPartition(partition);*/

        m_statsAgent = new StatsAgent();
    }

    public Procedure addProcedureForTest(String name)
    {
        Procedure retval = getCluster().getDatabases().get(m_databaseName).getProcedures().add(name);
        retval.setClassname(name);
        retval.setHasjava(true);
        retval.setSystemproc(false);
        return retval;
    }

    public void addHost(int hostId)
    {
        getCluster().getHosts().add(Integer.toString(hostId));
    }

//    public void addPartition(int partitionId)
//    {
//        getCluster().getPartitions().add(Integer.toString(partitionId));
//    }

    public void addSite(int siteId, int hostId, int partitionId, boolean isExec)
    {
        getCluster().getSites().add(Integer.toString(siteId));
        getSite(siteId).setHost(getHost(hostId));
        if (isExec)
        {
            getSite(siteId).getPartitions().add(getPartition(partitionId));
        }
        getSite(siteId).setIsup(true);
    }

    public void killSite(int siteId) {
        getSite(siteId).setIsup(false);
    }

    public void addSite(int siteId, int hostId, int partitionId, boolean isExec,
                        boolean isUp)
    {
        addSite(siteId, hostId, partitionId, isExec);
        getSite(siteId).setIsup(isUp);
    }

    public void addTable(String tableName, boolean isReplicated)
    {
        getDatabase().getTables().add(tableName);
        getTable(tableName).setIsreplicated(isReplicated);
    }

    public void addColumnToTable(String tableName, String columnName,
                                    VoltType columnType,
                                    boolean isNullable, String defaultValue,
                                    VoltType defaultType)
    {
        int index = getTable(tableName).getColumns().size();
        getTable(tableName).getColumns().add(columnName);
        getColumnFromTable(tableName, columnName).setIndex(index);
        getColumnFromTable(tableName, columnName).setType(columnType.getValue());
        getColumnFromTable(tableName, columnName).setNullable(isNullable);
//        getColumnFromTable(tableName, columnName).setName(columnName);
        getColumnFromTable(tableName, columnName).setDefaultvalue(defaultValue);
        getColumnFromTable(tableName, columnName).setDefaulttype(defaultType.getValue());
    }

    public Cluster getCluster()
    {
        return m_catalog.getClusters().get(m_clusterName);
    }

    public int getCrashCount() {
        return m_howManyCrashes;
    }

    Database getDatabase()
    {
        return getCluster().getDatabases().get(m_databaseName);
    }

    public Table getTable(String tableName)
    {
        return getDatabase().getTables().get(tableName);
    }

    Column getColumnFromTable(String tableName, String columnName)
    {
        return getTable(tableName).getColumns().get(columnName);
    }

    Host getHost(int hostId)
    {
        return getCluster().getHosts().get(String.valueOf(hostId));
    }

    Partition getPartition(int partitionId)
    {
        return CatalogUtil.getPartitionById(getCluster(), partitionId);
    }

    public Site getSite(int siteId)
    {
        return getCluster().getSites().get(String.valueOf(siteId));
    }

    @Override
    public String getBuildString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CatalogContext getCatalogContext()
    {
        m_context = new CatalogContext(m_catalog);
        return m_context;
    }

    @Override
    public Configuration getConfig()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setFaultDistributor(FaultDistributorInterface distributor)
    {
        m_faultDistributor = distributor;
    }

    @Override
    public FaultDistributorInterface getFaultDistributor()
    {
        return m_faultDistributor;
    }

    public void setHostMessenger(HostMessenger msg) {
        m_hostMessenger = msg;
    }

    @Override
    public HostMessenger getHostMessenger()
    {
        return m_hostMessenger;
    }

    @Override
    public Hashtable<Integer, PartitionExecutor> getLocalSites()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Messenger getMessenger()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VoltNetwork getNetwork()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setStatsAgent(StatsAgent agent)
    {
        m_statsAgent = agent;
    }

    @Override
    public StatsAgent getStatsAgent()
    {
        return m_statsAgent;
    }

    @Override
    public String getVersionString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean ignoreCrash()
    {
        // TODO Auto-generated method stub
        m_howManyCrashes++;
        return true;
    }

    @Override
    public void initialize(Configuration config)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isRunning()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void readBuildInfo()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdown(Thread mainSiteThread) throws InterruptedException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void startSampler()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void catalogUpdate(String diffCommands,
            String newCatalogURL, int expectedCatalogVersion,
            long currentTxnId)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getInstanceId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BackendTarget getBackendTargetType() {
        return BackendTarget.NONE;
    }

    @Override
    public void clusterUpdate(String diffCommands) {
        // TODO Auto-generated method stub

    }

    @Override
    public void logUpdate(String xmlConfig, long currentTxnId)
    {
        // TODO Auto-generated method stub

    }

}
