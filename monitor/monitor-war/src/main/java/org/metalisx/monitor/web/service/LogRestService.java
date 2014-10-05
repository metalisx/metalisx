package org.metalisx.monitor.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.metalisx.common.domain.dto.PageContextDto;
import org.metalisx.common.domain.dto.PageDto;
import org.metalisx.common.domain.utils.DatePrecision;
import org.metalisx.common.domain.utils.DateUtils;
import org.metalisx.common.gson.annotation.GsonTransient;
import org.metalisx.common.rest.dto.ItemDto;
import org.metalisx.common.rest.dto.ItemsDto;
import org.metalisx.common.rest.service.RestException;
import org.metalisx.monitor.domain.dto.MonitorOverviewItem;
import org.metalisx.monitor.domain.dto.MonitorSummary;
import org.metalisx.monitor.domain.filter.MonitorLogFilter;
import org.metalisx.monitor.domain.model.MonitorLog;
import org.metalisx.monitor.domain.service.MonitorLogService;
import org.metalisx.monitor.profiler.interceptor.Profile;
import org.metalisx.monitor.web.service.dto.ChartDto;
import org.metalisx.monitor.web.service.dto.ChartSettingsDto;
import org.metalisx.monitor.web.service.dto.OverviewDto;
import org.metalisx.monitor.web.service.dto.OverviewSettingsDto;


/**
 * REST service for monitor logs.
 * 
 * @author Stefan Oude Nijhuis
 */
@Profile
@Path("/logs")
public class LogRestService {

    @EJB
    private MonitorLogService monitorLogService;

    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto filter() {
        ItemDto itemDto = new ItemDto(new MonitorLogFilter());
        return itemDto;
    }

    @POST
    @Path("/overview")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OverviewDto getLogsOverview(MonitorLogFilter monitorLogFilter) {
    	PageContextDto<MonitorLogFilter> pageContextDto = new PageContextDto<MonitorLogFilter>();
    	pageContextDto.setFilter(monitorLogFilter);
        // We are interested in all values so we remove the default limit on the result
        pageContextDto.setLimit(null);
        PageDto<MonitorLog> pageDto = monitorLogService.findPage(pageContextDto);
        List<MonitorLog> items = pageDto.getItems();
        OverviewDto overviewDto = new OverviewDto();
        OverviewSettingsDto overviewSettingsDto = new OverviewSettingsDto();
        overviewSettingsDto.setMin(monitorLogFilter.getStartDate());
        overviewSettingsDto.setMax(monitorLogFilter.getEndDate());
        if (items.size() > 0) {
            Date startDate = items.get(0).getLogDate();
            Date endDate = items.get(items.size() - 1).getLogDate();
            DatePrecision datePrecision = DateUtils.getPrecision(startDate, endDate);
            overviewSettingsDto.setDatePrecision(datePrecision);
            overviewDto.setItems(getOverviewList(monitorLogFilter, datePrecision));
            // Make sure we have a min and max date in the settings.
            if (overviewSettingsDto.getMin() == null) {
            	overviewSettingsDto.setMin(startDate);
            }
            if (overviewSettingsDto.getMax() == null) {
            	overviewSettingsDto.setMax(endDate);
            }
        } else {
            overviewDto.setItems(new ArrayList<MonitorOverviewItem>());
        }
        overviewDto.setSettings(overviewSettingsDto);
        return overviewDto;
    }

    /**
     * Because every item in the overview list contains a date indicating the
     * beginning of a range, the end date of the last item is not in the list.
     * This way the last range is missing from the overview, to fix this a copy
     * of the last item is added with the end date of the range according to the
     * date precision. This will always result in a horizontal line at the end
     * of the overview.
     * 
     * The first and last item in the list should represent the boundary of the
     * overview, now it represent the date according to the datePrecision. This
     * means the first item contains a date earlier then the filter's start date
     * and the last item contains a date greater then the filter's end data(see
     * comment about the last range). To fix this the filter's start date is set
     * as date in the first item and the filter's end date is set as the date in
     * the last item.
     */
    private List<MonitorOverviewItem> getOverviewList(MonitorLogFilter monitorLogFilter, DatePrecision datePrecision) {
        List<MonitorOverviewItem> monitorOverviewItemList = monitorLogService.findOverviewByFilter(monitorLogFilter,
                datePrecision);
        if (monitorOverviewItemList.size() > 0) {
            MonitorOverviewItem lastMonitorOverviewItem = monitorOverviewItemList
                    .get(monitorOverviewItemList.size() - 1);
            // Fix missing end boundary
            MonitorOverviewItem monitorOverviewItem = new MonitorOverviewItem();
            monitorOverviewItem.setCount(lastMonitorOverviewItem.getCount());
            monitorOverviewItem.setDate(DateUtils.ceil(lastMonitorOverviewItem.getDate(), datePrecision.getNext()));
            monitorOverviewItem.setDuration(lastMonitorOverviewItem.getDuration());
            monitorOverviewItemList.add(monitorOverviewItem);
            // Fix start and end date of the boundary.
            monitorOverviewItemList.get(0).setDate(monitorLogFilter.getStartDate());
            monitorOverviewItemList.get(monitorOverviewItemList.size() - 1).setDate(monitorLogFilter.getEndDate());
        }
        return monitorOverviewItemList;
    }

    @POST
    @Path("/chart")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChartDto getLogsChart(MonitorLogFilter monitorLogFilter) {
        DateUtils.processDateRange(monitorLogFilter);
        PageContextDto<MonitorLogFilter> pageContextDto = new PageContextDto<MonitorLogFilter>();
        pageContextDto.setFilter(monitorLogFilter);
        // We are interested in all values so we remove the default limit on the result
        pageContextDto.setLimit(null);
        PageDto<MonitorLog> pageDto = monitorLogService.findPage(pageContextDto);
        List<MonitorLog> items = pageDto.getItems();
        ChartDto chartDto = new ChartDto();
        chartDto.setItems(items);
        ChartSettingsDto chartSettingsDto = new ChartSettingsDto();
        if (monitorLogFilter.getStartDate() != null) {
            chartSettingsDto.setMin(monitorLogFilter.getStartDate());
        } else if (items.size() > 0) {
            chartSettingsDto.setMin(items.get(0).getLogDate());
        }
        if (monitorLogFilter.getEndDate() != null) {
            chartSettingsDto.setMax(monitorLogFilter.getEndDate());
        } else if (items.size() > 0) {
            chartSettingsDto.setMax(items.get(items.size() - 1).getLogDate());
        }
        chartDto.setSettings(chartSettingsDto);
        return chartDto;
    }

    @POST
    @Path("/page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PageDto<MonitorLog> getLogsPage(PageContextDto<MonitorLogFilter> pageContextDto) {
        DateUtils.processDateRange(pageContextDto.getFilter());
        return monitorLogService.findPage(pageContextDto);
    }

    @GET
    @Path("/list-item/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto getListItemContent(@PathParam("id") Long id) {
        return new ItemDto(monitorLogService.findById(id));
    }

    @GET
    @Path("/request/tree/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDto getRequestTree(@PathParam("requestId") String requestId) {
        List<MonitorLog> list = monitorLogService.findByRequestId(requestId);
        Tree tree = getMonitorLogsAsTree(list);
        ItemDto itemDto = new ItemDto(tree);
        return itemDto;
    }

    /**
     * The <code>list</code> is converted to a hierarchical structure. The
     * conversion is required because the <code>list</code> has one problems:
     * the items with different levels are ordered in reverse. What makes it
     * difficult is the fact that the items with the same level are in the
     * correct order. For example the list contains log statements with the
     * following levels: 3 (A) 2 (B) 2 (C) 1 (D) 3 (E) 2 (F) 1 (G) The resulting
     * hierarchy tree must look like: 1 (D) 2 (B) 3 (A) 2 (C) 1 (G) 2 (F) 3 (E)
     */
    private Tree getMonitorLogsAsTree(List<MonitorLog> list) {
        Tree tree = new Tree();
        TreeNode currentTreeNode = null;
        // First we create the hierarchy from the end to the beginning of the
        // list.
        for (int i = list.size() - 1; i >= 0; i--) {
            MonitorLog monitorLog = list.get(i);
            if (currentTreeNode != null) {
                if (monitorLog.getDepth() == currentTreeNode.getMonitorLog().getDepth()) {
                    currentTreeNode = currentTreeNode.getParent();
                } else if (monitorLog.getDepth() < currentTreeNode.getMonitorLog().getDepth()) {
                    currentTreeNode = currentTreeNode.getParent().getParent();
                }
            }
            TreeNode treeNode = new TreeNode(monitorLog, currentTreeNode);
            if (currentTreeNode == null) {
                tree.getChildren().add(treeNode);
            } else {
                currentTreeNode.getChildren().add(treeNode);
            }
            currentTreeNode = treeNode;
        }
        // Now we need to reverse back the order of all items in the children
        // lists.
        swapChildren(tree.getChildren());
        return tree;
    }

    private void swapChildren(List<TreeNode> list) {
        if (list != null && list.size() > 0) {
            Collections.reverse(list);
            for (TreeNode childTreeNode : list) {
                swapChildren(childTreeNode.getChildren());
            }
        }
    }

    @POST
    @Path("/summary")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ItemsDto getSummary(MonitorLogFilter monitorLogFilter) {
    	PageContextDto<MonitorLogFilter> pageContextDto = new PageContextDto<MonitorLogFilter>();
    	pageContextDto.setFilter(monitorLogFilter);
        List<MonitorSummary> list = monitorLogService.findPageSummary(pageContextDto).getItems();
        ItemsDto itemsDto = new ItemsDto();
        itemsDto.setItems(list);
        return itemsDto;
    }

    private class Tree {

        List<TreeNode> children = new ArrayList<TreeNode>();

        public Tree() {
        }

        public List<TreeNode> getChildren() {
            return children;
        }

    }

    private class TreeNode {

        private MonitorLog monitorLog;

        @GsonTransient
        private TreeNode parent;

        private List<TreeNode> children = new ArrayList<TreeNode>();

        public TreeNode(MonitorLog monitorLog, TreeNode parent) {
            this.monitorLog = monitorLog;
            this.parent = parent;
        }

        public MonitorLog getMonitorLog() {
            return monitorLog;
        }

        public TreeNode getParent() {
            return parent;
        }

        public List<TreeNode> getChildren() {
            return children;
        }

    }

}
