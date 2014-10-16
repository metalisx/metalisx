<script type="text/ng-template" id="dynamic-control-panel.html">
	<div id="{{ngcId}}" class="form-horizontal" ngc-enter=".ngc-save">
		<dynamic-control-group ngc-entity="ngcEntity"></dynamic-control-group>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<a href="#" class="btn btn-primary ngc-save" ng-click="ngcSave($event)">Save</a>
				<a href="#" class="btn btn-primary ngc-cancel" ng-click="ngcCancel($event)">Cancel</a>
			</div>
		</div>
	</div>
</script>
<script type="text/ng-template" id="dynamic-control-group.html">
	<fieldset ng-repeat="field in ngcEntity.metadata.fields | entities:true">
		<div class="form-group">
			<label class="col-sm-2 control-label">{{field.name}}</label>
			<div class="col-sm-10">
				<dynamic-control ngc-entity="ngcEntity" ngc-field="field"></dynamic-control>
			</div>
		</div>
	</fieldset>
	<fieldset ng-repeat="field in ngcEntity.metadata.fields | entities:false | limitTo:1">
		<div class="form-group">
			<label class="col-sm-2 control-label">{{field.name}}</label>
			<div class="col-sm-10">
				<dynamic-control ngc-entity="ngcEntity" ngc-field="field" ngc-focus-enabled="true"></dynamic-control>
			</div>
		</div>
	</fieldset>
	<fieldset ng-repeat="field in ngcEntity.metadata.fields | entities:false | startFrom:1">
		<div class="form-group">
			<label class="col-sm-2 control-label">{{field.name}}</label>
			<div class="col-sm-10">
				<dynamic-control ngc-entity="ngcEntity" ngc-field="field" ngc-focus-enabled="false"></dynamic-control>
			</div>
		</div>
	</fieldset>
</script>
<script type="text/ng-template" id="dynamic-control-input.html">
	<input id="{{ngcField.name}}" type="text" class="form-control" ng-model="ngcEntity.item[ngcField.name]" ng-disabled="ngcField.isPrimaryKey" 
		ngc-focus ngc-focus-enabled="{{ngcFocusEnabled}}"/>
</script>
<script type="text/ng-template" id="dynamic-control-date-input.html">
	<input id="{{ngcField.name}}" type="text" class="form-control" ng-model="ngcEntity.item[ngcField.name]" ng-disabled="ngcField.isPrimaryKey" 
		ngc-datepicker ngc-datepicker-show-timepicker="true" ngc-focus ngc-focus-enabled="{{ngcFocusEnabled}}"/>
</script>
<script type="text/ng-template" id="dynamic-control-textarea.html">
	<textarea id="{{ngcField.name}}" type="text" class="form-control" ng-model="ngcEntity.item[ngcField.name]" ng-disabled="ngcField.isPrimaryKey" 
		ngc-ckeditor ngc-focus ngc-focus-enabled="{{ngcFocusEnabled}}"/>
</script>
