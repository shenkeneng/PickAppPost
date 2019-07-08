/**
 * <p>
 * Copyright: Copyright (c) 2014
 * Company: ZTE
 * Description: ����д����ļ��Ǹ�ʲô�õ�
 * </p>
 * @Title BaseAdapter.java
 * @Package com.example.haojihui.adapter
 * @version 1.0
 * @author wsy
 * @date 2014-10-8
 */
package com.frxs.PickApp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class MyBaseAdapter<T> extends android.widget.BaseAdapter
{
   private Context context;
   private List<T> list;
   
   public MyBaseAdapter(Context context, List<T> list)
   {
      this.context = context;
      this.list = list;
   }
   
   public Context getContext()
   {
      return context;
   }
   
   @Override
   public int getCount()
   {
      if (null == list || list.size() == 0)
      {
         return 0;
      }
      
      return list.size();
   }

   @Override
   public Object getItem(int position)
   {
      if (null == list || list.size() == 0)
      {
         return null;
      }
      
      return list.get(position);
   }

   @Override
   public long getItemId(int position)
   {
      return position;
   }
   
   protected abstract View getItemView(int position, View convertView, ViewGroup parent);
   
   @Override
   public View getView(int position, View convertView, ViewGroup parent)
   {
      return getItemView(position, convertView, parent);
   }
   
   protected View inflateItemView(int resId)
   {
     return ((LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resId, null);
   }

}