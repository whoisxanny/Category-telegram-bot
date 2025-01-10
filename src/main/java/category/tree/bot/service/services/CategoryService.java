package category.tree.bot.service.services;


import category.tree.bot.entity.Category;

import java.util.List;

public interface CategoryService {


    String viewTree();

    Category addElement(String parent, String child);

    String removeElement(String element);

    List<String> getHelp();


}
