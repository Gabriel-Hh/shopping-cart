package com.shashi.service.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shashi.beans.DemandBean;
import com.shashi.beans.ProductBean;
import com.shashi.service.ProductService;
import com.shashi.utility.DBUtil;
import com.shashi.utility.IDUtil;
import com.shashi.utility.MailMessage;

public class ProductServiceImpl implements ProductService {

	public static final int minStockQuantity = 3;

	@Override
	public String addProduct(String prodName, String prodType, String prodInfo, double prodPrice, int prodQuantity,
			InputStream prodImage, String prodQuality, int prodDiscount) {
		String status = null;
		String prodId = IDUtil.generateId();

		ProductBean product = new ProductBean(prodId, prodName, prodType, prodInfo, prodPrice, prodQuantity, prodImage, prodQuality, prodDiscount);

		status = addProduct(product);

		return status;
	}

	@Override
	public String addProduct(ProductBean product) {
		String status = "Product Registration Failed!";

		if (product.getProdId() == null)
			product.setProdId(IDUtil.generateId());

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("insert into product values(?,?,?,?,?,?,?,?,?);");
			ps.setString(1, product.getProdId());
			ps.setString(2, product.getProdName());
			ps.setString(3, product.getProdType());
			ps.setString(4, product.getProdInfo());
			ps.setDouble(5, product.getProdPrice());
			ps.setInt(6, product.getProdQuantity());
			ps.setBlob(7, product.getProdImage());
			ps.setString(6, product.getProdQuality());
			ps.setInt(7, product.getProdDiscount());

			int k = ps.executeUpdate();

			if (k > 0) {

				status = "Product Added Successfully with Product Id: " + product.getProdId();

			} else {

				status = "Product Updation Failed!";
			}

		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return status;
	}

	@Override
	public String removeProduct(String prodId) {
		String status = "Product Removal Failed!";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;

		try {
			ps = con.prepareStatement("delete from product where pid=?");
			ps.setString(1, prodId);

			int k = ps.executeUpdate();

			if (k > 0) {
				status = "Product Removed Successfully!";

				ps2 = con.prepareStatement("delete from usercart where prodid=?");

				ps2.setString(1, prodId);

				ps2.executeUpdate();

			}

		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(ps2);

		return status;
	}

	@Override
	public String updateProduct(ProductBean prevProduct, ProductBean updatedProduct) {
		String status = "Product Updation Failed!";

		if (!prevProduct.getProdId().equals(updatedProduct.getProdId())) {

			status = "Both Products are Different, Updation Failed!";

			return status;
		}

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement(
					"update product set pname=?,ptype=?,pinfo=?,pprice=?,pquantity=?,image=?,quality=?,discount=? where pid=?");

			ps.setString(1, updatedProduct.getProdName());
			ps.setString(2, updatedProduct.getProdType());
			ps.setString(3, updatedProduct.getProdInfo());
			ps.setDouble(4, updatedProduct.getProdPrice());
			ps.setInt(5, updatedProduct.getProdQuantity());
			ps.setBlob(6, updatedProduct.getProdImage());
			ps.setString(7, updatedProduct.getProdQuality());
			ps.setInt(8, updatedProduct.getProdDiscount());
			ps.setString(9, prevProduct.getProdId());

			int k = ps.executeUpdate();

			if (k > 0)
				status = "Product Updated Successfully!";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return status;
	}

	@Override
	public String updateProductPrice(String prodId, double updatedPrice) {
		String status = "Price Updation Failed!";

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("update product set pprice=? where pid=?");

			ps.setDouble(1, updatedPrice);
			ps.setString(2, prodId);

			int k = ps.executeUpdate();

			if (k > 0)
				status = "Price Updated Successfully!";
		} catch (SQLException e) {
			status = "Error: " + e.getMessage();
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return status;
	}

	@Override
	public List<ProductBean> getAllProducts() {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product");

			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString(1));
				product.setProdName(rs.getString(2));
				product.setProdType(rs.getString(3));
				product.setProdInfo(rs.getString(4));
				product.setProdPrice(rs.getDouble(5));
				product.setProdQuantity(rs.getInt(6));
				product.setProdImage(rs.getAsciiStream(7));
				product.setProdQuality(rs.getString(8));
				product.setProdDiscount(rs.getInt(9));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public List<ProductBean> getAllProductsByType(String type) {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT * FROM `shopping-cart`.product where lower(ptype) like ?;");
			ps.setString(1, "%" + type + "%");
			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString(1));
				product.setProdName(rs.getString(2));
				product.setProdType(rs.getString(3));
				product.setProdInfo(rs.getString(4));
				product.setProdPrice(rs.getDouble(5));
				product.setProdQuantity(rs.getInt(6));
				product.setProdImage(rs.getAsciiStream(7));
				product.setProdQuality(rs.getString(8));
				product.setProdDiscount(rs.getInt(9));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public List<ProductBean> searchAllProducts(String search) {
		List<ProductBean> products = new ArrayList<ProductBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(
					"SELECT * FROM `shopping-cart`.product where lower(ptype) like ? or lower(pname) like ? or lower(pinfo) like ?");
			search = "%" + search + "%";
			ps.setString(1, search);
			ps.setString(2, search);
			ps.setString(3, search);
			rs = ps.executeQuery();

			while (rs.next()) {

				ProductBean product = new ProductBean();

				product.setProdId(rs.getString(1));
				product.setProdName(rs.getString(2));
				product.setProdType(rs.getString(3));
				product.setProdInfo(rs.getString(4));
				product.setProdPrice(rs.getDouble(5));
				product.setProdQuantity(rs.getInt(6));
				product.setProdImage(rs.getAsciiStream(7));
				product.setProdQuality(rs.getString(8));
				product.setProdDiscount(rs.getInt(9));

				products.add(product);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return products;
	}

	@Override
	public byte[] getImage(String prodId) {
		byte[] image = null;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select image from product where  pid=?");

			ps.setString(1, prodId);

			rs = ps.executeQuery();

			if (rs.next())
				image = rs.getBytes("image");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(rs);

		return image;
	}

	@Override
	public ProductBean getProductDetails(String prodId) {
		ProductBean product = null;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product where pid=?");

			ps.setString(1, prodId);
			rs = ps.executeQuery();

			if (rs.next()) {
				product = new ProductBean();
				product.setProdId(rs.getString(1));
				product.setProdName(rs.getString(2));
				product.setProdType(rs.getString(3));
				product.setProdInfo(rs.getString(4));
				product.setProdPrice(rs.getDouble(5));
				product.setProdQuantity(rs.getInt(6));
				product.setProdImage(rs.getAsciiStream(7));
				product.setProdQuality(rs.getString(8));
				product.setProdDiscount(rs.getInt(9));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return product;
	}

	@Override
	public String updateProductWithoutImage(String prevProductId, ProductBean updatedProduct) {
		String status = "Product Updation Failed!";

		if (!prevProductId.equals(updatedProduct.getProdId())) {

			status = "Both Products are Different, Updation Failed!";

			return status;
		}

		int prevQuantity = new ProductServiceImpl().getProductQuantity(prevProductId);
		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("update product set pname=?,ptype=?,pinfo=?,pprice=?,pquantity=?,quality=?,discount=? where pid=?");

			ps.setString(1, updatedProduct.getProdName());
			ps.setString(2, updatedProduct.getProdType());
			ps.setString(3, updatedProduct.getProdInfo());
			ps.setDouble(4, updatedProduct.getProdPrice());
			ps.setInt(5, updatedProduct.getProdQuantity());
			ps.setString(6, updatedProduct.getProdQuality());
			ps.setInt(7, updatedProduct.getProdDiscount());
			ps.setString(8, prevProductId);

			int k = ps.executeUpdate();
			// System.out.println("prevQuantity: "+prevQuantity);
			if ((k > 0) && (prevQuantity < updatedProduct.getProdQuantity())) {
				status = "Product Updated Successfully!";
				// System.out.println("updated!");
				List<DemandBean> demandList = new DemandServiceImpl().haveDemanded(prevProductId);

				for (DemandBean demand : demandList) {

					String userFName = new UserServiceImpl().getFName(demand.getUserName());
					try {
						MailMessage.productAvailableNow(demand.getUserName(), userFName, updatedProduct.getProdName(),
								prevProductId);
					} catch (Exception e) {
						System.out.println("Mail Sending Failed: " + e.getMessage());
					}
					boolean flag = new DemandServiceImpl().removeProduct(demand.getUserName(), prevProductId);

					if (flag)
						status += " And Mail Send to the customers who were waiting for this product!";
				}
			} else if (k > 0)
				status = "Product Updated Successfully!";
			else
				status = "Product Not available in the store!";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		// System.out.println("Prod Update status : "+status);

		return status;
	}

	@Override
	public double getProductPrice(String prodId) {
		double price = 0;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product where pid=?");

			ps.setString(1, prodId);
			rs = ps.executeQuery();

			if (rs.next()) {
				price = rs.getDouble("pprice");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return price;
	}

	@Override
	public boolean sellNProduct(String prodId, int n) {
		boolean flag = false;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;

		try {

			ps = con.prepareStatement("update product set pquantity=(pquantity - ?) where pid=?");

			ps.setInt(1, n);

			ps.setString(2, prodId);

			int k = ps.executeUpdate();

			if (k > 0)
				flag = true;
		} catch (SQLException e) {
			flag = false;
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return flag;
	}

	@Override
	public int getProductQuantity(String prodId) {

		int quantity = 0;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from product where pid=?");

			ps.setString(1, prodId);
			rs = ps.executeQuery();

			if (rs.next()) {
				quantity = rs.getInt("pquantity");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);

		return quantity;
	}
	
	public List<ProductBean> getProductsByQuality(String type, String quality) {
	    List<ProductBean> products = new ArrayList<>();

	    String query = "SELECT * FROM `shopping-cart`.product WHERE lower(ptype) LIKE ? AND `quality` = ?;";

	    try (Connection con = DBUtil.provideConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {
	        
	        ps.setString(1, "%" + type.toLowerCase() + "%");
	        ps.setString(2, quality);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                ProductBean product = new ProductBean();
	                product.setProdId(rs.getString(1));
	                product.setProdName(rs.getString(2));
	                product.setProdType(rs.getString(3));
	                product.setProdInfo(rs.getString(4));
	                product.setProdPrice(rs.getDouble(5));
	                product.setProdQuantity(rs.getInt(6));
	                product.setProdImage(rs.getAsciiStream(7));
	                product.setProdQuality(rs.getString(8));
					product.setProdDiscount(rs.getInt(9));

	                products.add(product);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return products;
	}
	// get a product sold of a specific type
	public List<ProductBean> getProductsBySales(String type) {
	    List<ProductBean> products = new ArrayList<>();

	    //String query = "SELECT * FROM `shopping-cart`.product WHERE lower(ptype) LIKE ? AND sold > 0 ORDER BY sold DESC;";
	    String query = "SELECT product.*, SUM(orders.quantity) as sold "
	    		+ "FROM `shopping-cart`.product LEFT JOIN `shopping-cart`.orders "
	    		+ "ON product.pid = orders.prodid "
	    		+ "WHERE lower(product.ptype) LIKE ? GROUP BY product.pid ORDER BY sold DESC";

	    try (Connection con = DBUtil.provideConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {
	        
	        ps.setString(1, "%" + type.toLowerCase() + "%");

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                ProductBean product = new ProductBean();
	                product.setProdId(rs.getString(1)); 
	                product.setProdName(rs.getString(2));
	                product.setProdType(rs.getString(3));
	                product.setProdInfo(rs.getString(4));
	                product.setProdPrice(rs.getDouble(5)); 
	                product.setProdQuantity(rs.getInt(6));
	                product.setProdImage(rs.getAsciiStream(7));
					product.setProdQuality(rs.getString(8));
					product.setProdDiscount(rs.getInt(9));
	                product.setProdSold(rs.getInt(10));

	                products.add(product);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return products;
	}
	
	public List<ProductBean> getAllProductsSold()
	{	
		List<ProductBean> products = new ArrayList<>();
		String query = "SELECT product.*, SUM(orders.quantity) as sold "
				+ "FROM `shopping-cart`.product LEFT JOIN `shopping-cart`.orders "
				+ "ON product.pid = orders.prodid "
				+ "GROUP BY product.pid ORDER BY sold DESC";
		try (Connection con = DBUtil.provideConnection();
		         PreparedStatement ps = con.prepareStatement(query)) {

		        try (ResultSet rs = ps.executeQuery()) {
		            while (rs.next()) {
		                ProductBean product = new ProductBean();
		                product.setProdId(rs.getString("pid"));
		                product.setProdName(rs.getString("pname"));
		                product.setProdType(rs.getString("ptype"));
		                product.setProdInfo(rs.getString("pinfo"));
		                product.setProdPrice(rs.getDouble("pprice"));
		                product.setProdQuantity(rs.getInt("pquantity"));
		                product.setProdImage(rs.getAsciiStream("image"));
		                product.setProdSold(rs.getInt("sold")); // Set the total sold
						product.setProdQuality(rs.getString("quality"));
						product.setProdDiscount(rs.getInt("discount"));

		                products.add(product);
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

		    return products;
	}

	public List<ProductBean> getProductsByDiscounts(String type) {
	    List<ProductBean> products = new ArrayList<>();

	    String query = "SELECT * FROM `shopping-cart`.product WHERE lower(ptype) LIKE ? AND discount > 0 ORDER BY discount DESC;";

	    try (Connection con = DBUtil.provideConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {
	        
	        ps.setString(1, "%" + type.toLowerCase() + "%");

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                ProductBean product = new ProductBean();
	                product.setProdId(rs.getString(1)); 
	                product.setProdName(rs.getString(2));
	                product.setProdType(rs.getString(3));
	                product.setProdInfo(rs.getString(4));
	                product.setProdPrice(rs.getDouble(5)); 
	                product.setProdQuantity(rs.getInt(6));
	                product.setProdImage(rs.getAsciiStream(7));
					product.setProdQuality(rs.getString(8));
	                product.setProdDiscount(rs.getInt(9));

	                products.add(product);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return products;
	}

	private List<ProductBean> getSales(String sort) {
	    List<ProductBean> products = new ArrayList<>();

	    String query = "SELECT product.*, IFNULL(SUM(orders.quantity), 0) as sold "
	            + "FROM `shopping-cart`.product LEFT JOIN `shopping-cart`.orders "
	            + "ON product.pid = orders.prodid "
	            + "GROUP BY product.pid ORDER BY sold " + sort;

	    try (Connection con = DBUtil.provideConnection();
	         PreparedStatement ps = con.prepareStatement(query)) {

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                ProductBean product = new ProductBean();
	                product.setProdId(rs.getString("pid"));
	                product.setProdName(rs.getString("pname"));
	                product.setProdType(rs.getString("ptype"));
	                product.setProdInfo(rs.getString("pinfo"));
	                product.setProdPrice(rs.getDouble("pprice"));
	                product.setProdQuantity(rs.getInt("pquantity"));
	                product.setProdImage(rs.getAsciiStream("image"));
	                product.setProdSold(rs.getInt("sold"));
					product.setProdQuality(rs.getString("quality"));

	                products.add(product);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return products;
	}

	public List<ProductBean> getLeastSelling() {
	    
	    return getSales("ASC");
	}
	
	public List<ProductBean> getBestSelling() {
	    
	    return getSales("DESC");
	}
	
	public void SendMailOnMinStockThreshold(String prodId)
	{
		try
		{
			ProductBean product = getProductDetails(prodId);

			if(product == null) throw new Exception("Unexpected result: Product was null. Mail not sent.");

			if(product.getProdQuantity() <= minStockQuantity)
			{
				MailMessage.lowQuantity(product.getProdName(), product.getProdId(), product.getProdQuantity(), minStockQuantity);
				System.out.println("");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
