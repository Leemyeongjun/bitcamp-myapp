package bitcamp.myapp.listener;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import bitcamp.myapp.dao.BoardDao;
import bitcamp.myapp.dao.BoardFileDao;
import bitcamp.myapp.dao.MemberDao;
import bitcamp.myapp.dao.StudentDao;
import bitcamp.myapp.dao.TeacherDao;
import bitcamp.myapp.service.impl.DefaultBoardService;
import bitcamp.myapp.service.impl.DefaultStudentService;
import bitcamp.myapp.service.impl.DefaultTeacherService;
import bitcamp.util.BitcampSqlSessionFactory;
import bitcamp.util.Controller;
import bitcamp.util.DaoGenerator;
import bitcamp.util.RequestHandlerMapping;
import bitcamp.util.RequestMapping;
import bitcamp.util.TransactionManager;

@WebListener
public class ContextLoaderListener implements ServletContextListener {

  List<Class<?>> controllerClasses = new ArrayList<>();

  List<Object> servicePool = new ArrayList<>();

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      InputStream mybatisConfigInputStream = Resources.getResourceAsStream(
          "bitcamp/myapp/config/mybatis-config.xml");
      SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
      BitcampSqlSessionFactory sqlSessionFactory = new BitcampSqlSessionFactory(
          builder.build(mybatisConfigInputStream));

      TransactionManager txManager = new TransactionManager(sqlSessionFactory);

      BoardDao boardDao = new DaoGenerator(sqlSessionFactory).getObject(BoardDao.class);
      MemberDao memberDao = new DaoGenerator(sqlSessionFactory).getObject(MemberDao.class);
      StudentDao studentDao = new DaoGenerator(sqlSessionFactory).getObject(StudentDao.class);
      TeacherDao teacherDao = new DaoGenerator(sqlSessionFactory).getObject(TeacherDao.class);
      BoardFileDao boardFileDao = new DaoGenerator(sqlSessionFactory).getObject(BoardFileDao.class);

      servicePool.add(new DefaultBoardService(txManager, boardDao, boardFileDao));
      servicePool.add(new DefaultStudentService(txManager, memberDao, studentDao));
      servicePool.add(new DefaultTeacherService(txManager, memberDao, teacherDao));

      // ????????? ???????????? ???????????? ????????????.
      ServletContext ctx = sce.getServletContext();

      // ??? ?????????????????? ???????????? ???????????? ?????? ?????? ????????????
      // ?????? ???????????? PageController ?????? ????????? ??????
      findPageController(new File(ctx.getRealPath("/WEB-INF/classes")), "");

      // ????????? ??????????????? ???????????? ????????????
      createPageControllers(ctx);

    } catch (Exception e) {
      System.out.println("??? ?????????????????? ????????? ???????????? ?????? ?????? ??????!");
      e.printStackTrace();
    }
  }

  private void findPageController(File dir, String packageName) throws Exception {
    File[] files = dir.listFiles(file -> file.isDirectory() || file.getName().endsWith(".class"));

    if (packageName.length() > 0) {
      packageName += ".";
    }

    for (File file : files) {
      String qName = packageName + file.getName(); // ???????????? + ?????????  ???) bitcamp.myapp.vo
      if (file.isDirectory()) {
        findPageController(file, qName);
      } else {
        Class<?> clazz = Class.forName(qName.replace(".class", ""));
        if (clazz.isInterface()) { // ?????????????????? ??????
          continue;
        }
        Controller anno = clazz.getAnnotation(Controller.class);
        if (anno != null) {
          controllerClasses.add(clazz);
        }
      }
    }
  }

  private void createPageControllers(ServletContext ctx) throws Exception {
    for (Class<?> c : controllerClasses) {
      Constructor<?> constructor = c.getConstructors()[0];
      Parameter[] params = constructor.getParameters();
      Object[] arguments = prepareArguments(params);
      Object controller = constructor.newInstance(arguments);

      // ????????? ?????????????????? RequestMapping ?????????????????? ?????? ???????????? ??????
      // ServletContext ???????????? ????????????.
      Method[] methods = c.getDeclaredMethods();
      for (Method m : methods) {
        RequestMapping anno = m.getAnnotation(RequestMapping.class);
        if (anno == null) continue;
        ctx.setAttribute(anno.value(), new RequestHandlerMapping(controller, m));
        System.out.println(c.getName() + "." + m.getName() + "() ?????? ????????? ??????!");
      }

    }
  }

  private Object[] prepareArguments(Parameter[] params) {
    Object[] arguments = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      for (Object obj : servicePool) {
        if (params[i].getType().isInstance(obj)) {
          arguments[i] = obj;
          break;
        }
      }
    }
    return arguments;
  }


}






