package org.jamocha.service;

import java.util.ArrayList;
import java.util.List;

import woolfel.examples.model.Hobby;

import junit.framework.TestCase;

public class ServiceConfigTest extends TestCase {
	public ServiceConfigTest() {
		super();
	}
	
	public void testSaveServiceConfig() {
		ServiceConfiguration config = new ServiceConfiguration();
		config.setServiceName("sample");
		RuleApplicationBean app1 = new RuleApplicationBean();
		config.setApplications(new ArrayList<RuleApplicationBean>());
		config.getApplications().add(app1);
		app1.setMinPool(1);
		app1.setMaxPool(10);
		app1.setInitialPool(1);
		app1.setName("test");
		
		app1.setFunctionGroups(new ArrayList<FunctionPackage>());
		app1.setObjectData(new ArrayList<ObjectData>());
		app1.setRulesets(new ArrayList<ClipsRuleset>());
		app1.setModels(new ArrayList<ObjectModel>());
		
		ObjectModel model1 = new ObjectModel();
		app1.getModels().add(model1);
		ArrayList<String> classList = new ArrayList<String>();
		model1.setClassList(classList);
		classList.add("woolfel.examples.model.Account");
		classList.add("woolfel.examples.model.Account2");
		
		FunctionPackage functionGroup = new FunctionPackage();
		app1.getFunctionGroups().add(functionGroup);
		String[] functionList = new String[]{"woolfel.examples.function.HellFunction"};
		functionGroup.setClassNames(functionList);
		
		ClipsRuleset ruleset1 = new ClipsRuleset();
		app1.getRulesets().add(ruleset1);
		ruleset1.setURL("./samples/join_sample1.clp");
		
		ClipsInitialData initialData1 = new ClipsInitialData();
		app1.getClipsData().add(initialData1);
		initialData1.setURL("./samples/data/data.dat");
		
		JSONData<?> jdata = new JSONData<>();
		jdata.setName("org.jamocha.examples.model.Account");
		jdata.setUrl("./samples/configuration/data.json");
		
		List<JSONData<?>> jsondata1 = new ArrayList<JSONData<?>>();
		jsondata1.add(jdata);
		app1.setJsonData(jsondata1);
		
		RuleServiceImpl.saveConfiguration("./samples/configuration/test_config.json", config);
	}
	
	public void testSaveServiceConfig2() {
		ServiceConfiguration config = new ServiceConfiguration();
		config.setServiceName("sample");
		RuleApplicationBean app1 = new RuleApplicationBean();
		config.setApplications(new ArrayList<RuleApplicationBean>());
		config.getApplications().add(app1);
		app1.setMinPool(1);
		app1.setMaxPool(10);
		app1.setInitialPool(1);
		app1.setName("test");
		
		app1.setFunctionGroups(new ArrayList<FunctionPackage>());
		app1.setObjectData(new ArrayList<ObjectData>());
		app1.setRulesets(new ArrayList<ClipsRuleset>());
		app1.setModels(new ArrayList<ObjectModel>());
		
		ObjectModel model1 = new ObjectModel();
		app1.getModels().add(model1);
		ArrayList<String> classList = new ArrayList<String>();
		model1.setClassList(classList);
		classList.add("woolfel.examples.model.Account");
		classList.add("woolfel.examples.model.Account2");
		
		FunctionPackage functionGroup = new FunctionPackage();
		app1.getFunctionGroups().add(functionGroup);
		String[] functionList = new String[]{"woolfel.examples.function.HellFunction"};
		functionGroup.setClassNames(functionList);
		
		ClipsRuleset ruleset1 = new ClipsRuleset();
		app1.getRulesets().add(ruleset1);
		ruleset1.setURL("./samples/join_sample1.clp");
		
		ClipsInitialData initialData1 = new ClipsInitialData();
		app1.getClipsData().add(initialData1);
		initialData1.setURL("./samples/data/data.dat");
		
		ObjectData objectData1 = new ObjectData();
		objectData1.setName("generic");
		app1.getObjectData().add(objectData1);
		objectData1.setUrl("./samples/data/hobbies.xml");
		
		RuleServiceImpl.saveConfiguration("./samples/configuration/test_config2.json", config);
	}
	
	public void testSaveObjectData() {
		ArrayList<Hobby> list = new ArrayList<Hobby>();
		Hobby hobby1 = new Hobby();
		list.add(hobby1);
		hobby1.setName("Hiking");
		hobby1.setHobbyCode("0101");
		hobby1.setCategory("Sport");
		hobby1.setSubCategory("Outdoor");
		
		Hobby hobby2 = new Hobby();
		list.add(hobby2);
		hobby2.setName("Climbing");
		hobby2.setHobbyCode("0201");
		hobby2.setCategory("Sport");
		hobby2.setSubCategory("Outdoor");
		
		Hobby hobby3 = new Hobby();
		list.add(hobby3);
		hobby3.setName("Swimming");
		hobby3.setCategory("Sport");
		hobby3.setSubCategory("Indoor/Outdoor");
		
		ObjectData.saveObjectData("./samples/data/data1.json", list);
	}
	
	public void testCreateServiceInstance() {
		RuleService service = RuleServiceImpl.createInstance("./samples/configuration/sample_config.json");
		assertNotNull(service);
		System.out.println(service.getServiceName());
		assertNotNull(service.getServiceName());
	}
}
