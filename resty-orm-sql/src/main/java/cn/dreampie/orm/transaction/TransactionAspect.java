package cn.dreampie.orm.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wangrenhui on 15/1/2.
 */
public class TransactionAspect implements Aspect {

  private DsTransactionExcutor[] excutors;
  private int index = 0;

  public void aspect(InvocationHandler ih, Object proxy, Method method, Object[] args) throws Throwable {
    if (index == 0) {
      Transaction transactionAnn = method.getAnnotation(Transaction.class);
      if (transactionAnn != null) {
        String[] names = transactionAnn.name();
        int[] levels = transactionAnn.level();
        excutors = new DsTransactionExcutor[names.length];
        for (int i = 0; i < names.length; i++) {
          excutors[i] = new DsTransactionExcutor(names[i], levels.length == 1 ? levels[0] : levels[i]);
        }
      }
    }

    if (excutors.length > 0) {
      if (index < excutors.length)
        excutors[index++].transaction(this, ih, proxy, method, args);
      else if (index++ == excutors.length) {
        index = 0;
        excutors = null;
        ih.invoke(proxy, method, args);
        return;
      }
    }
  }

}