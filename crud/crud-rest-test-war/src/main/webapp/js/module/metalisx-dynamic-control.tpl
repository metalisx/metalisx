<script type="text/ng-template" id="dynamic-control-panel.html">
	<div id="{{ngcId}}" class="form-horizontal" ngc-enter=".ngc-save">
		<dynamic-control-group ngc-entity="ngcEntity"></dynamic-control-group>
		<div class="form-group">
			<div class="col-sm-offset-4 col-sm-8">
				<a href="#" class="btn btn-primary ngc-save" ng-click="ngcSave()">Save</a>
				<a href="#" class="btn btn-primary ngc-cancel" ng-click="ngcCancel()">Cancel</a>
			</div>
		</div>
	</div>
</script>
<script type="text/ng-template" id="dynamic-control-group.html">
	<fieldset ng-repeat="field in ngcEntity.metadata.fields">
		<div class="form-group">
			<label class="col-sm-4 control-label">{{field.name}}</label>
			<div class="col-sm-8">
				<dynamic-control ngc-entity="ngcEntity" ngc-field="field"></dynamic-control>
			</div>
		</div>
	</fieldset>
</script>
<script type="text/ng-template" id="dynamic-control-input.html">
	<input type="text" class="form-control" ng-model="ngcEntity.item[ngcField.name]" ng-disabled="ngcField.isPrimaryKey"/>
</script>
<script type="text/ng-template" id="dynamic-control-date-input.html">
	<input type="text" class="form-control" ng-model="ngcEntity.item[ngcField.name]" ng-disabled="ngcField.isPrimaryKey" ngc-date-milliseconds/>
</script>