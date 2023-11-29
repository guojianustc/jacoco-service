//package com.xcmg.jacocoservice.jacococlient;
//
//import com.test.testmanagement.model.TestClass;
//import com.test.testmanagement.model.TestMethod;
//import com.test.testmanagement.model.TestPackage;
//import com.test.testmanagement.model.TestProject;
//import com.test.testmanagement.service.TestClassService;
//import com.test.testmanagement.service.TestMethodService;
//import com.test.testmanagement.service.TestPackageService;
//import com.test.testmanagement.service.TestProjectService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.File;
//import java.util.Date;
//
//public class ParseJacocoReport {
//
//    /*Spring-Mybatis*/
//    @Autowired
//    private TestProjectService service;
//    @Autowired
//    private TestPackageService packages;
//    @Autowired
//    private TestClassService classes;
//    @Autowired
//    private TestMethodService funtions;
//
//
//    String testname = "2019-07-26";
//
////    String baseUri = "/data/Mock/testmanagement/file/umemid-airport/it-coveragereport/";
//
//
//    Element element_project_name = null;
//    String pom_project_name = "";
//    Document root_doc = null;
//
//
//    public void JacocoReportToDB(String htmlPath) throws Exception {
//        File root_input = new File(htmlPath + "index.html");
//
//
//        /*开始读取Jacoco覆盖率并存储到数据库*/
//        TestProject testProject = new TestProject();
//        testProject.setTestname(testname);
//        Date d = new Date();
//        testProject.setStarttime(d);//获取starttime
////        testProject.setStarttime(String.valueOf(System.currentTimeMillis()));//获取starttime
//
//
//        root_doc = Jsoup.parse(root_input, "UTF-8");
//        element_project_name = root_doc.select("H1").first();//查找第一个H1元素
//        pom_project_name = element_project_name.text();
//        testProject.setProjectname(pom_project_name);
//
//        Integer project_id = service.insertTestProject(testProject);//插入项目
//
//        //1)project_name
//        //UmeTest
//        System.out.println(pom_project_name);
//
//        //2)包名
//            /*
//            com.umetrip.unittest.plugins.dbReporter
//            com.umetrip.unittest
//            com.umetrip.unittest.testcase
//            com.umetrip.unittest.plugins.htmlReporter
//             */
//        Elements elements_index_package = root_doc.getElementsByClass("el_package");
//        Integer i = 0;
//        for (Element packagename : elements_index_package) {
//            TestPackage testPackage = new TestPackage();
//            /******************************************/
//            System.out.println(packagename.text());//包名com.umetrip.unittest.plugins.dbReporter  ...
//            testPackage.setPackagename(packagename.text());
//            testPackage.setTestproject_id(project_id);
//            testPackage.setTestname(testname);
//
//            Integer packageid = packages.insertTestPackage(testPackage);//插入包
//
//
//            File class_input = new File(htmlPath + packagename.text() + "/index.html");
//            Document class_doc = Jsoup.parse(class_input, "UTF-8");
//            Elements class_names = class_doc.getElementsByClass("el_class");
//
//            Integer j = 0;
//            for (Element class_name : class_names) {
//                TestClass testClass = new TestClass();
//                testClass.setClassname(class_name.text());
//                testClass.setTestpackage_id(packageid);
//                testClass.setTestname(testname);
//                Integer classid = classes.insertTestClass(testClass);//插入类
//                System.out.println("    " + class_name.text());//类名TestReportListener
//                if (class_name.text().contains(".")) {
//                    continue;
//                }
//
//                File method_input = new File(htmlPath + packagename.text() + "/" + class_name.text() + ".html");
//                Document method_doc = Jsoup.parse(method_input, "UTF-8");
//                Elements method_names = method_doc.getElementsByClass("el_method");
//                TestMethod testMethod = new TestMethod();
//
//
//                /********************************************************************************************/
//                Element tbody = method_doc.getElementsByTag("tbody").first();
//                Elements trs = tbody.getElementsByTag("tr");
//                testMethod.setTestclass_id(classid);
//                for (Element tr : trs) {
//                    Elements tds = tr.getElementsByTag("td");
//
//                    Integer td_counter = 0;
//                    for (Element td : tds) {
//                        System.out.println(td.text());
//                        switch (td_counter) {
//                            case 0://方法名
//                                System.out.println("方法名");
//                                Element methodName = td.getElementsByTag("a").first();
//                                testMethod.setMethodname(methodName.text());
//                                td_counter = td_counter + 1;
//                                break;
//                            case 1: //missedinstructions(百分比条)
//                                Elements imgs = td.getElementsByTag("img");
//
//                                StringBuffer missedInstructions = new StringBuffer();
//                                for (Element img : imgs) {
//                                    if (img.attr("src").contains("green")) {
//                                        missedInstructions.append("|覆盖:" + img.attr("title") + "|");
//                                    } else if (img.attr("src").contains("red")) {
//                                        missedInstructions.append("|未覆盖:" + img.attr("title") + "|");
//                                    }
//
//                                }
//                                testMethod.setMissedinstructions(missedInstructions.toString());
//                                td_counter = td_counter + 1;
//                                break;
//                            case 2://missed coverage
//                                testMethod.setInstructioncoverage(td.text());
//                                td_counter = td_counter + 1;
//                                break;
//
//                            case 3://missed branches(百分比条）
//                                Elements img1s = td.getElementsByTag("img");
//                                StringBuffer missedBranches = new StringBuffer();
//                                for (Element img : img1s) {
//                                    if (img.attr("src").contains("green")) {
//                                        missedBranches.append("|覆盖:" + img.attr("title") + "|");
//                                    } else if (img.attr("src").contains("red")) {
//                                        missedBranches.append("|未覆盖:" + img.attr("title") + "|");
//                                    }
//                                }
//                                testMethod.setMissedbranches(missedBranches.toString());
//                                td_counter = td_counter + 1;
//                                break;
//
//                            case 4://branch coverage
//                                testMethod.setBranchcoverage(td.text());
//                                td_counter = td_counter + 1;
//                                break;
//
//                            case 5://missed cxty
//                                testMethod.setMissedcxty(td.text());
//                                td_counter = td_counter + 1;
//                                break;
//
//                            case 6://cxty
//                                testMethod.setTotalcxty(td.text());
//                                td_counter = td_counter + 1;
//                                break;
//
//                            case 7://missed lines
//                                testMethod.setMissedlines(td.text());
//                                td_counter = td_counter + 1;
//                                break;
//
//                            case 8://lines
//                                testMethod.setTotallines(td.text());
//                                td_counter = td_counter + 1;
//                                break;
//
//                            case 9://missed method
//                                testMethod.setMissedmethods(td.text());
//                                td_counter = td_counter + 1;
//                                break;
//
//                            case 10://methods
//                                testMethod.setTotalmethods(td.text());
//                                td_counter = td_counter + 1;
//                                break;
//
//                                    /*default:
//                                        //System.out.println("default");
//                                        break;
//                                        */
//                        }
//                    }
//                    testMethod.setTestname(testname);
//                    funtions.insertTestMethod(testMethod);
//                }
//                /*****************************************************************/
//
//
//                Element tfoot = method_doc.getElementsByTag("tfoot").first();
//                Element tr = tfoot.getElementsByTag("tr").first();
//                Elements tds = tfoot.getElementsByTag("td");
//
//                StringBuffer class_coverage = new StringBuffer();
//                class_coverage.append(class_name.text() + "#");
//                for (Element td : tds) {
//                    if (td.text().contains("Total")) {
//                        continue;
//                    }
//                    //System.out.println(td.text());
//
//                    class_coverage.append(td.text());
//                    class_coverage.append("#");
//                }
//                System.out.println("%%%%%%%%%%" + class_name.text() + "类整体覆盖");
//                System.out.println(class_coverage);
//
//
//                TestClass someClassTotalSummary = new TestClass();
//                someClassTotalSummary.setTestname(testname);
//                someClassTotalSummary.setId(classid);
//
//                someClassTotalSummary.setMissedinstructions(class_coverage.toString().split("#")[1]);
//                someClassTotalSummary.setInstructioncoverage(class_coverage.toString().split("#")[2]);
//
//                someClassTotalSummary.setMissedbranches(class_coverage.toString().split("#")[3]);
//                someClassTotalSummary.setBranchcoverage(class_coverage.toString().split("#")[4]);
//
//                someClassTotalSummary.setMissedcxty(class_coverage.toString().split("#")[5]);
//                someClassTotalSummary.setTotalcxty(class_coverage.toString().split("#")[6]);
//
//                someClassTotalSummary.setMissedlines(class_coverage.toString().split("#")[7]);
//                someClassTotalSummary.setTotallines(class_coverage.toString().split("#")[8]);
//
//                someClassTotalSummary.setMissedmethods(class_coverage.toString().split("#")[9]);
//                someClassTotalSummary.setTotalmethods(class_coverage.toString().split("#")[10]);
//
//
//                classes.updateTestClass(someClassTotalSummary);//更新Class表
//
//                j = j + 1;
//            }
//
//            Element tfoot = class_doc.getElementsByTag("tfoot").first();
//            Element tr = tfoot.getElementsByTag("tr").first();
//            Elements tds = tfoot.getElementsByTag("td");
//
//            StringBuffer package_coverage = new StringBuffer();
//            package_coverage.append(packagename.text() + "#");
//            for (Element td : tds) {
//                if (td.text().contains("Total")) {
//                    continue;
//                }
//                //System.out.println(td.text());
//
//                package_coverage.append(td.text());
//                package_coverage.append("#");
//            }
//            System.out.println("&&&&&&&&&&" + packagename.text() + "包整体覆盖");
//            System.out.println(package_coverage);
//            //更新Package表
//            TestPackage somePackageTotalSummary = new TestPackage();
//
//            somePackageTotalSummary.setId(packageid);
//
//            somePackageTotalSummary.setMissedinstructions(package_coverage.toString().split("#")[1]);
//            somePackageTotalSummary.setInstructioncoverage(package_coverage.toString().split("#")[2]);
//
//            somePackageTotalSummary.setMissedbranches(package_coverage.toString().split("#")[3]);
//            somePackageTotalSummary.setBranchcoverage(package_coverage.toString().split("#")[4]);
//
//            somePackageTotalSummary.setMissedcxty(package_coverage.toString().split("#")[5]);
//            somePackageTotalSummary.setTotalcxty(package_coverage.toString().split("#")[6]);
//
//            somePackageTotalSummary.setMissedlines(package_coverage.toString().split("#")[7]);
//            somePackageTotalSummary.setTotallines(package_coverage.toString().split("#")[8]);
//
//            somePackageTotalSummary.setMissedmethods(package_coverage.toString().split("#")[9]);
//            somePackageTotalSummary.setTotalmethods(package_coverage.toString().split("#")[10]);
//
//            somePackageTotalSummary.setMissedclasses(package_coverage.toString().split("#")[11]);
//            somePackageTotalSummary.setTotalclasses(package_coverage.toString().split("#")[12]);
//
//            packages.updateTestPackageById(somePackageTotalSummary);
//
//            i = i + 1;
//        }//package for
//
//        Element tfoot = root_doc.getElementsByTag("tfoot").first();
//        Element tr = tfoot.getElementsByTag("tr").first();
//        Elements tds = tfoot.getElementsByTag("td");
//
//        StringBuffer total_coverage = new StringBuffer();
//        total_coverage.append(pom_project_name + "#");
//        for (Element td : tds) {
//            if (td.text().contains("Total")) {
//                continue;
//            }
//            //System.out.println(td.text());
//
//            total_coverage.append(td.text());
//            total_coverage.append("#");
//        }
//        System.out.println("================" + pom_project_name + "项目整体覆盖");
//        System.out.println(total_coverage);
//
//        TestProject someProjectTotalSummary = new TestProject();
//
//        someProjectTotalSummary.setId(project_id);
//
//        someProjectTotalSummary.setMissedinstructions(total_coverage.toString().split("#")[1]);
//        someProjectTotalSummary.setInstructioncoverage(total_coverage.toString().split("#")[2]);
//
//        someProjectTotalSummary.setMissedbranches(total_coverage.toString().split("#")[3]);
//        someProjectTotalSummary.setBranchcoverage(total_coverage.toString().split("#")[4]);
//
//        someProjectTotalSummary.setMissedcxty(total_coverage.toString().split("#")[5]);
//        someProjectTotalSummary.setTotalcxty(total_coverage.toString().split("#")[6]);
//
//        someProjectTotalSummary.setMissedlines(total_coverage.toString().split("#")[7]);
//        someProjectTotalSummary.setTotallines(total_coverage.toString().split("#")[8]);
//
//        someProjectTotalSummary.setMissedmethods(total_coverage.toString().split("#")[9]);
//        someProjectTotalSummary.setTotalmethods(total_coverage.toString().split("#")[10]);
//
//        someProjectTotalSummary.setMissedclasses(total_coverage.toString().split("#")[11]);
//        someProjectTotalSummary.setTotalclasses(total_coverage.toString().split("#")[12]);
//
//        service.updateTestProjectById(someProjectTotalSummary);
//
//
//    }
//
//    public static void main(String[] args) {
//        ParseJacocoReport parseJacocoReport = new ParseJacocoReport();
//        try {
//            parseJacocoReport.JacocoReportToDB("/data/Mock/testmanagement/file/umemid-airport/it-coveragereport/");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//
