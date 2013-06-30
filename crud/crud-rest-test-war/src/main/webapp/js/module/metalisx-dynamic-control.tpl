<script type="text/ng-template" id="dynamic-control-panel.html">
	<div id="{{ngcId}}" ngc-enter=".ngc-save">
		<dynamic-control-group ngc-entity="ngcEntity"></dynamic-control-group>
		<div class="buttonContainer">
			<a href="#" class="btn btn-primary ngc-save" ng-click="ngcSave()">Save</a>
			<a href="#" class="btn btn-primary ngc-cancel" ng-click="ngcCancel()">Cancel</a>
		</div>
	</div>
</script>
<script type="text/ng-template" id="dynamic-control-group.html">
	<fieldset ng-repeat="field in ngcEntity.metadata.fields">
		<label>{{field.name}}</label>
		<dynamic-control ngc-entity="ngcEntity" ngc-field="field"></dynamic-control>
		<br/>
	</fieldset>
</script>
<script type="text/ng-template" id="dynamic-control-input.html">
	<input type="text" ng-model="ngcEntity.item[ngcField.name]" ng-disabled="ngcField.isPrimaryKey"/>
</script>
<script type="text/ng-template" id="dynamic-control-date-input.html">
	<input type="text" ng-model="ngcEntity.item[ngcField.name]" ng-disabled="ngcField.isPrimaryKey" ngc-date-milliseconds/>
</script>